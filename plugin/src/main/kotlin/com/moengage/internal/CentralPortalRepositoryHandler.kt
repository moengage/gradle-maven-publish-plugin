package com.moengage.internal

import com.moengage.internal.model.ArtifactReleasePortal
import com.moengage.internal.repository.CentralPortalRepository
import com.moengage.internal.repository.Repository
import com.moengage.internal.utils.LogLevel
import com.moengage.internal.utils.getArtifactReleasePath
import com.moengage.internal.utils.log
import org.gradle.api.file.Directory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

private const val defaultReleasingType = "AUTOMATIC"

/**
 * Repository handler for the Maven Central Portal
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
internal class CentralPortalRepositoryHandler(private val repository: CentralPortalRepository) : Repository {

    private val tag = "${BASE_TAG}_CentralPortalRepositoryHandler"

    /**
     * Close and release with [defaultReleasingType]
     * @since 1.0.0
     */
    fun closeAndRelease(artifactId: String, artifactDirectory: Directory, stagedRepositoryId: String): String {
        log(message = "$tag closeAndRelease(): artifactId = $artifactId, artifactDirectory = $artifactDirectory, stagedRepositoryId = $stagedRepositoryId")
        val path = getArtifactReleasePath(false, stagedRepositoryId, artifactDirectory, ArtifactReleasePortal.CENTRAL_PORTAL)
        val directory = File(path.substringAfter("://"))
        val zipFile = File("${directory.absolutePath}.zip")
        val out = ZipOutputStream(FileOutputStream(zipFile))
        directory.walkTopDown().forEach {
            if (it.isDirectory) {
                return@forEach
            }
            if (it.name.contains("maven-metadata")) {
                return@forEach
            }

            val entry = ZipEntry(it.toRelativeString(directory))
            out.putNextEntry(entry)
            out.write(it.readBytes())
            out.closeEntry()
        }
        out.close()

        val deploymentName = "$artifactId-$stagedRepositoryId"
        val publishId = repository.uploadArtifact(deploymentName, defaultReleasingType, zipFile)
        if (publishId != null) {
            log(LogLevel.NOTICE, "$tag closeAndRelease(): stagedRepositoryId = $stagedRepositoryId release with publishId = $publishId")
            return publishId
        }
        throw IOException("Failed to publish $deploymentName")
    }
}
