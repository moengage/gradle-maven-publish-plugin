package com.moengage.internal.repository.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * Interface containing all different network requests used by maven central portal
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
internal interface CentralPortalService {

    /**
     * Upload the artifact to the portal and release if [publishingType] equals automatically
     * @return the deployment id, which can be used to perform action on the artifact
     * @since 0.0.1
     */
    @Multipart
    @POST("publisher/upload")
    fun uploadRepository(
        @Query("name") name: String?,
        @Query("publishingType") publishingType: String?,
        @Part input: MultipartBody.Part
    ): Call<String>
}
