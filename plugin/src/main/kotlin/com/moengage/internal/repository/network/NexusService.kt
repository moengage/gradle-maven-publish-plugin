package com.moengage.internal.repository.network

import com.moengage.internal.model.NexusArtifactCreateRequest
import com.moengage.internal.model.NexusArtifactCreateResponse
import com.moengage.internal.model.NexusPromoteRequest
import com.moengage.internal.model.NexusStagingRepository
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface NexusService {

    @GET("staging/profile_repositories/{profileId}")
    fun getStagedRepositories(@Path("profileId") profileId: String): Call<NexusStagingRepository>

    @POST("staging/profile_repositories/{profileId}/start")
    fun createRepository(
        @Path("profileId") profileId: String,
        @Body createRequest: NexusArtifactCreateRequest
    ): Call<NexusArtifactCreateResponse>

    @POST("staging/profile_repositories/{profileId}/finish")
    fun closeRepository(
        @Path("profileId") profileId: String,
        @Body promoteRequest: NexusPromoteRequest
    ): Call<Unit>

    @POST("staging/profile_repositories/{profileId}/promote")
    fun promoteRepository(
        @Path("profileId") profileId: String,
        @Body promoteRequest: NexusPromoteRequest
    ): Call<Unit>

    @POST("staging/profile_repositories/{profileId}/drop")
    fun dropRepository(
        @Path("profileId") profileId: String,
        @Body promoteRequest: NexusPromoteRequest
    ): Call<Unit>
}
