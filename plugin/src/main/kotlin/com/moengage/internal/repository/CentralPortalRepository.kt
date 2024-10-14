package com.moengage.internal.repository

import com.moengage.internal.BASE_TAG
import com.moengage.internal.exception.NetworkCallException
import com.moengage.internal.repository.network.CentralPortalService
import com.moengage.internal.utils.LogLevel
import com.moengage.internal.utils.log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Repository class for CentralPortal, uses [CentralPortalService] for network calls
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
internal class CentralPortalRepository(private val service: CentralPortalService) {

    private val tag = "${BASE_TAG}_CentralPortalRepository"

    /**
     * Upload the artifact to the portal and release if [publishingType] equals automatically
     *
     * @param name readable name for the artifact
     * @param publishingType "AUTOMATIC" / "USER_MANAGED"
     * @param file artifact file to upload
     * @return the deployment id, which can be used to perform action on the artifact
     *
     * @since 0.0.1
     */
    fun uploadArtifact(name: String, publishingType: String, file: File): String? {
        val uploadFile = RequestBody.create(MediaType.get("application/octet-stream"), file)
        val multipart = MultipartBody.Part.createFormData("bundle", file.name, uploadFile)
        val uploadResponse = service.uploadRepository(name, publishingType, multipart).execute()

        if (!uploadResponse.isSuccessful) {
            val failureCode = uploadResponse.code()
            val failureMessage = uploadResponse.message()

            log(LogLevel.ERROR, "$tag uploadArtifact(): Failure Code: $failureCode, Failure Message: $failureMessage")
            throw NetworkCallException("Failed to upload $name due to response code: $failureCode and response message: $failureMessage")
        }
        return uploadResponse.body()
    }
}
