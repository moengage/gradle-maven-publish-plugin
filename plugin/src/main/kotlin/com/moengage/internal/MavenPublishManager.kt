package com.moengage.internal

import com.moengage.internal.model.ArtifactReleasePortal
import com.moengage.internal.repository.CentralPortalRepository
import com.moengage.internal.repository.NexusRepository
import com.moengage.internal.repository.Repository
import com.moengage.internal.repository.network.ServiceBuilder
import com.moengage.internal.utils.LogLevel
import com.moengage.internal.utils.getArtifactReleasePath
import com.moengage.internal.utils.getUserName
import com.moengage.internal.utils.getUserPassword
import com.moengage.internal.utils.isSnapshotBuild
import com.moengage.internal.utils.log
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import java.util.UUID

internal class MavenPublishManager(private val project: Project) {

    private val tag = "${BASE_TAG}_MavenPublishManager"

    private lateinit var buildDirectory: Directory
    private lateinit var releaseVersion: String
    private lateinit var releaseArtifactId: String
    private lateinit var releaseGroupId: String
    private lateinit var releaseVariant: String
    private lateinit var releasePortal: ArtifactReleasePortal
    private lateinit var repository: Repository

    init {
        initializeRequiredProperties()
    }

    /**
     * Initialize all the required properties
     */
    private fun initializeRequiredProperties() {
        log(message = "$tag initializeRequiredProperties(): Started")
        buildDirectory = project.rootProject.layout.buildDirectory.get()
        releaseVersion = project.findProperty(VERSION_NAME) as String
        releaseArtifactId = project.findProperty(ARTIFACT_NAME) as String
        releaseGroupId = project.findProperty(GROUP) as String
        releaseVariant = project.findProperty(RELEASE_VARIANT) as? String ?: DEFAULT_RELEASE_VARIANT
        releasePortal = ArtifactReleasePortal.getMavenCentralPortal(project.findProperty(RELEASE_HOST) as? String)

        val serviceBuilder =
            ServiceBuilder(releasePortal, project.getUserName(releasePortal), project.getUserPassword(releasePortal))
        repository = if (releasePortal == ArtifactReleasePortal.CENTRAL_PORTAL) {
            CentralPortalRepositoryHandler(CentralPortalRepository(serviceBuilder.getCentralPortalService()))
        } else {
            NexusRepositoryHandler(
                NexusRepository(serviceBuilder.getNexusService(), project.findProperty(PROFILE_ID) as String)
            )
        }
        log(message = "$tag initializeRequiredProperties(): Completed")
    }

    /**
     * Configure the release
     * @since 1.0.0
     */
    fun configurePublish(provider: Provider<String>) {
        log(message = "$tag configurePublish(): Started")
        project.extensions.configure(PublishingExtension::class.java) {
            publications {
                repositories {
                    maven {
                        if (releasePortal != ArtifactReleasePortal.CENTRAL_PORTAL) {
                            credentials {
                                username = project.getUserName(releasePortal)
                                password = project.getUserPassword(releasePortal)
                            }
                        }

                        setUrl(
                            provider.map {
                                getArtifactReleasePath(
                                    isSnapshotBuild(releaseVersion),
                                    it,
                                    buildDirectory,
                                    releasePortal
                                )
                            }
                        )
                    }
                }

                register(releaseVariant, MavenPublication::class.java) {
                    project.afterEvaluate {
                        from(project.components.getByName(releaseVariant))
                    }
                    groupId = releaseGroupId
                    artifactId = releaseArtifactId
                    version = releaseVersion

                    configurePom()
                }
            }
        }
        log(message = "$tag configurePublish(): Completed")
    }

    /**
     * Configure singing
     * @since 1.0.0
     */
    fun configureSigning() {
        log(message = "$tag configureSigning(): Started")
        val isInMemorySigningEnabled = project.findProperty(SIGNING_TYPE) as? Boolean ?: false
        log(LogLevel.NOTICE, "$tag configureSigning(): isInMemorySigningEnabled = $isInMemorySigningEnabled")
        project.extensions.configure(SigningExtension::class.java) {
            if (isInMemorySigningEnabled) {
                useInMemoryPgpKeys(
                    project.findProperty(SIGNING_IN_MEMORY_KEY_ID) as String,
                    project.findProperty(SIGNING_IN_MEMORY_KEY) as String,
                    project.findProperty(SIGNING_IN_MEMORY_KEY_PASSWORD) as String
                )
            }
            sign(project.extensions.getByType(PublishingExtension::class.java).publications.getByName(releaseVariant))
        }
        log(message = "$tag configureSigning(): Completed")
    }

    /**
     * Return the id to be used for staging the repository
     * @since 1.0.0
     */
    fun getStagedRepositoryId(): String {
        val stagedRepositoryId =
            if (releasePortal == ArtifactReleasePortal.CENTRAL_PORTAL || isSnapshotBuild(releaseVersion)) {
                UUID.randomUUID().toString()
            } else {
                (repository as NexusRepositoryHandler).getStagedRepositoryId(
                    releaseGroupId,
                    releaseArtifactId,
                    releaseVersion
                )
            }
        log(LogLevel.NOTICE, "$tag getStagedRepositoryId(): $stagedRepositoryId")
        return stagedRepositoryId
    }

    /**
     * Close and release the repository once it is staged
     * @since 1.0.0
     */
    fun closeAndReleaseRepository(stagedRepositoryIdProvider: Provider<String>) {
        log(message = "$tag closeAndReleaseArtifact(): Started")
        if (isSnapshotBuild(releaseVersion)) {
            log(message = "$tag closeAndReleaseRepository(): no need to close and release snapshot build")
            return
        }
        if (releasePortal == ArtifactReleasePortal.CENTRAL_PORTAL) {
            log(message = "$tag closeAndReleaseArtifact(): releasing on central portal")
            (repository as CentralPortalRepositoryHandler).closeAndRelease(
                releaseArtifactId,
                buildDirectory,
                stagedRepositoryIdProvider.get()
            )
        } else {
            log(message = "$tag closeAndReleaseArtifact(): releasing on nexus portal")
            (repository as NexusRepositoryHandler).closeAndRelease(stagedRepositoryIdProvider.get())
        }
        log(message = "$tag closeAndReleaseArtifact(): Completed")
    }

    private fun MavenPublication.configurePom() {
        log(message = "$tag configurePom(): Started")
        pom {
            name.set(project.findProperty(NAME) as? String)
            description.set(project.findProperty(POM_DESCRIPTION) as? String)
            url.set(project.findProperty(POM_URL) as? String)
            licenses {
                license {
                    name.set(project.findProperty(POM_LICENCE_NAME) as? String)
                    url.set(project.findProperty(POM_LICENCE_URL) as? String)
                }
            }
            developers {
                developer {
                    id.set(project.findProperty(POM_DEVELOPER_ID) as? String)
                    name.set(project.findProperty(POM_DEVELOPER_NAME) as? String)
                    email.set(project.findProperty(POM_DEVELOPER_EMAIL) as? String)
                }
            }
            scm {
                url.set(project.findProperty(POM_SCM_URL) as? String)
                connection.set(project.findProperty(POM_SCM_CONNECTION) as? String)
                developerConnection.set(project.findProperty(POM_SCM_DEV_CONNECTION) as? String)
            }
        }
        log(message = "$tag configurePom(): Completed")
    }
}
