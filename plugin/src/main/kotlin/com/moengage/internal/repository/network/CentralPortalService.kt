package com.moengage.internal.repository.network

import com.moengage.internal.model.CentralPortalDeploymentStatus
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

internal interface CentralPortalService {

    @Multipart
    @POST("publisher/upload")
    fun uploadRepository(
        @Query("name") name: String?,
        @Query("publishingType") publishingType: String?,
        @Part input: MultipartBody.Part
    ): Call<String>

    @POST("publisher/status")
    fun getRepositoryStatus(@Query("id") id: String): Call<CentralPortalDeploymentStatus>
}
