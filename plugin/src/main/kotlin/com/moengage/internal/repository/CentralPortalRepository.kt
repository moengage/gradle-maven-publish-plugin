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
 * @since 1.0.0
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
     * @since 1.0.0
     */
    fun uploadArtifact(name: String, publishingType: String, file: File): String? {
        val uploadFile = RequestBody.create(MediaType.get("application/octet-stream"), file)
        val multipart = MultipartBody.Part.createFormData("bundle", file.name, uploadFile)
        val uploadResponse = service.uploadRepository(name, publishingType, multipart).execute()

        if (!uploadResponse.isSuccessful) {
            log(LogLevel.ERROR, "$tag uploadArtifact(): ${uploadResponse.errorBody()}")
            throw NetworkCallException("Failed to upload artifact with name - $name")
        }
        return uploadResponse.body()
    }
}
