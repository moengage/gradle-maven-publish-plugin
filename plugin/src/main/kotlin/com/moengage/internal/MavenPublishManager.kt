package com.moengage.internal

import com.moengage.internal.model.ArtifactReleasePortal
import com.moengage.internal.repository.CentralPortalRepository
import com.moengage.internal.repository.NexusRepository
import com.moengage.internal.repository.Repository
import com.moengage.internal.repository.network.ServiceBuilder
import com.moengage.internal.repository.network.defaultTimeout
import com.moengage.internal.repository.network.maximumRetryCountForNexusRepository
import com.moengage.internal.repository.network.maximumTimeOutDurationForNetworkCall
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
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.gradle.api.Action
import java.util.UUID
import kotlin.math.min

internal class MavenPublishManager(private val project: Project) {

    private val tag = "${BASE_TAG}_MavenPublishManager"

    private lateinit var buildDirectory: Directory
    private lateinit var releaseVersion: String
    private lateinit var releaseArtifactId: String
    private lateinit var releaseGroupId: String
    private lateinit var releaseVariant: String
    private lateinit var releasePortal: ArtifactReleasePortal
    private lateinit var repository: Repository
    private var isKmpProject: Boolean = false

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

        val maxRetryCountOnTimeOut = min(
            (project.findProperty(NEXUS_REPOSITORY_MAX_RETRY_ON_TIMEOUT) as? String)?.toInt() ?: 0,
            maximumRetryCountForNexusRepository
        )
        val networkTimeoutDuration = min(
            (project.findProperty(NETWORK_CALL_TIMEOUT_DURATION) as? String)?.toLong() ?: defaultTimeout,
            maximumTimeOutDurationForNetworkCall
        )

        val serviceBuilder = ServiceBuilder(
            releasePortal,
            project.getUserName(releasePortal),
            project.getUserPassword(releasePortal),
            networkTimeoutDuration
        )

        repository = if (releasePortal == ArtifactReleasePortal.CENTRAL_PORTAL) {
            CentralPortalRepositoryHandler(CentralPortalRepository(serviceBuilder.getCentralPortalService()))
        } else {
            NexusRepositoryHandler(
                NexusRepository(
                    serviceBuilder.getNexusService(),
                    project.findProperty(PROFILE_ID) as String,
                    maxRetryCountOnTimeOut
                )
            )
        }

        // Set group and version early so the KMP plugin can generate the correct Gradle Module Metadata routing table
        project.group = releaseGroupId
        project.version = releaseVersion

        log(message = "$tag initializeRequiredProperties(): Config { releaseVersion = $releaseVersion, releaseArtifactId = $releaseArtifactId, releaseGroupId = $releaseGroupId, releaseVariant = $releaseVariant, releasePortal = $releasePortal, maxRetryCountOnTimeOut = $maxRetryCountOnTimeOut, networkTimeoutDuration = $networkTimeoutDuration }")
        log(message = "$tag initializeRequiredProperties(): Completed")
    }

    /**
     * Configure the release
     * @since 0.0.1
     */
    fun configurePublish(provider: Provider<String>) {
        log(message = "$tag configurePublish(): Started")
        isKmpProject = project.plugins.hasPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID)
        log(message = "$tag configurePublish(): isKmpProject = $isKmpProject")

        if (isKmpProject) {
            project.pluginManager.withPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
                project.extensions.configure(KotlinMultiplatformExtension::class.java, object : Action<KotlinMultiplatformExtension> {
                    override fun execute(ext: KotlinMultiplatformExtension) {
                        ext.targets.configureEach(object : Action<KotlinTarget> {
                            override fun execute(target: KotlinTarget) {
                                if (target is KotlinAndroidTarget) {
                                    if (target.publishLibraryVariants.isNullOrEmpty()) {
                                        target.publishLibraryVariants = listOf(releaseVariant)
                                    }
                                }
                            }
                        })
                    }
                })
            }
        }

        project.extensions.configure(PublishingExtension::class.java) {
            repositories {
                maven {
                    if (releasePortal != ArtifactReleasePortal.CENTRAL_PORTAL || isSnapshotBuild(releaseVersion)) {
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

            if (isKmpProject) {
                // KMP plugin auto-registers publications. We are overriding artifactId using project.afterEvaluate to prevent KMP from resetting it.
                publications.withType(MavenPublication::class.java).configureEach {
                    val publication = this
                    publication.groupId = releaseGroupId
                    publication.version = releaseVersion
                    publication.configurePom()
                    
                    project.afterEvaluate(object : Action<Project> {
                        override fun execute(p: Project) {
                            val projectName = p.name
                            if (publication.artifactId == projectName) {
                                publication.artifactId = releaseArtifactId
                            } else if (publication.artifactId.startsWith("$projectName-")) {
                                publication.artifactId = publication.artifactId.replaceFirst("$projectName-", "$releaseArtifactId-")
                            }
                            log(message = "$tag configurePublish(): KMP publication configured: name=${publication.name}, artifactId=${publication.artifactId}")
                        }
                    })
                }
            } else {
                publications {
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
        }

        log(message = "$tag configurePublish(): Completed")
    }

    /**
     * Configure signing
     * @since 0.0.1
     */
    fun configureSigning() {
        log(message = "$tag configureSigning(): Started")
        val isInMemorySigningEnabled = (project.findProperty(SIGNING_TYPE) as? String ?: "false").toBoolean()
        project.extensions.configure(SigningExtension::class.java) {
            if (isInMemorySigningEnabled) {
                useInMemoryPgpKeys(
                    project.findProperty(SIGNING_IN_MEMORY_KEY_ID) as String,
                    project.findProperty(SIGNING_IN_MEMORY_KEY) as String,
                    project.findProperty(SIGNING_IN_MEMORY_KEY_PASSWORD) as String
                )
            }
            val publications = project.extensions.getByType(PublishingExtension::class.java).publications
            if (isKmpProject) {
                sign(publications)
            } else {
                sign(publications.getByName(releaseVariant))
            }
        }
        log(message = "$tag configureSigning(): Completed")
    }

    /**
     * Return the id to be used for staging the repository
     * @since 0.0.1
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
     * @since 0.0.1
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
