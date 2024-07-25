package com.moengage.internal.utils

import com.moengage.internal.BASE_TAG
import com.moengage.internal.MAVEN_CENTER_PASSWORD
import com.moengage.internal.MAVEN_CENTER_USER_NAME
import com.moengage.internal.OSS_MAVEN_CENTER_PASSWORD
import com.moengage.internal.OSS_MAVEN_CENTER_USER_NAME
import com.moengage.internal.SNAPSHOT_IDENTIFIER
import com.moengage.internal.SO1_OSS_MAVEN_CENTER_PASSWORD
import com.moengage.internal.SO1_OSS_MAVEN_CENTER_USER_NAME
import com.moengage.internal.model.ArtifactReleasePortal
import org.gradle.api.Project
import org.gradle.api.file.Directory

private const val tag = "${BASE_TAG}_Utils"

/**
 * Return the username for the maven release portal based on the [releasingPortal]
 * @since 1.0.0
 */
internal fun Project.getUserName(releasingPortal: ArtifactReleasePortal): String {
    return when (releasingPortal) {
        ArtifactReleasePortal.S01_OSS_PORTAL -> findProperty(SO1_OSS_MAVEN_CENTER_USER_NAME) as String
        ArtifactReleasePortal.OSS_PORTAL -> findProperty(OSS_MAVEN_CENTER_USER_NAME) as String
        ArtifactReleasePortal.CENTRAL_PORTAL -> findProperty(MAVEN_CENTER_USER_NAME) as String
    }
}

/**
 * Return the user password for the maven release portal based on the [releasingPortal]
 * @since 1.0.0
 */
internal fun Project.getUserPassword(releasingPortal: ArtifactReleasePortal): String {
    return when (releasingPortal) {
        ArtifactReleasePortal.S01_OSS_PORTAL -> findProperty(SO1_OSS_MAVEN_CENTER_PASSWORD) as String
        ArtifactReleasePortal.OSS_PORTAL -> findProperty(OSS_MAVEN_CENTER_PASSWORD) as String
        ArtifactReleasePortal.CENTRAL_PORTAL -> findProperty(MAVEN_CENTER_PASSWORD) as String
    }
}

/**
 * Return true if the [releaseVersion] is the snapshot version
 * @since 1.0.0
 */
internal fun isSnapshotBuild(releaseVersion: String) = releaseVersion.endsWith(SNAPSHOT_IDENTIFIER)

/**
 * Return the artifact release path
 * @since 1.0.0
 */
internal fun getArtifactReleasePath(
    isSnapshotRelease: Boolean,
    stagedArtifactId: String,
    buildDirectory: Directory,
    releasingPortal: ArtifactReleasePortal
): String {
    val releasingHostUri = when (releasingPortal) {
        ArtifactReleasePortal.CENTRAL_PORTAL -> {
            if (isSnapshotRelease) {
                throw IllegalArgumentException("Snapshot is not supported with Maven Central Portal.")
            } else {
                "file://$buildDirectory/publish/staging/$stagedArtifactId"
            }
        }

        ArtifactReleasePortal.OSS_PORTAL -> {
            if (isSnapshotRelease) {
                releasingPortal.baseStagingHostUrl!!
            } else {
                "${releasingPortal.baseHostUrl}staging/deployByRepositoryId/$stagedArtifactId/"
            }
        }

        ArtifactReleasePortal.S01_OSS_PORTAL -> {
            if (isSnapshotRelease) {
                releasingPortal.baseStagingHostUrl!!
            } else {
                "${releasingPortal.baseHostUrl}staging/deployByRepositoryId/$stagedArtifactId/"
            }
        }
    }

    log(LogLevel.NOTICE, "$tag getArtifactReleasePath(): Releasing Uri = $releasingHostUri")
    return releasingHostUri
}
