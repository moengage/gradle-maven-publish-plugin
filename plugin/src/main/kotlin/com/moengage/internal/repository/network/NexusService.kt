package com.moengage.internal.repository.network

import com.moengage.internal.model.NexusPromoteRequest
import com.moengage.internal.model.NexusRepositoryCreateRequest
import com.moengage.internal.model.NexusRepositoryCreateResponse
import com.moengage.internal.model.NexusStagingRepositoryData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interface containing all different network requests used by oss nexus portal
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
internal interface NexusService {

    /**
     * Create a new staged repository with the required details [NexusRepositoryCreateRequest]
     * @since 0.0.1
     */
    @POST("staging/profiles/{profileId}/start")
    fun createRepository(
        @Path("profileId") profileId: String,
        @Body createRequest: NexusRepositoryCreateRequest
    ): Call<NexusRepositoryCreateResponse>

    /**
     * Close the open staged repository for the given repository id
     * @since 0.0.1
     */
    @POST("staging/profiles/{profileId}/finish")
    fun closeRepository(
        @Path("profileId") profileId: String,
        @Body promoteRequest: NexusPromoteRequest
    ): Call<Unit>

    /**
     * Move the staged repository to next state for the given repository id
     * @since 0.0.1
     */
    @POST("staging/profiles/{profileId}/promote")
    fun promoteRepository(
        @Path("profileId") profileId: String,
        @Body promoteRequest: NexusPromoteRequest
    ): Call<Unit>

    /**
     * Drop the staged repository for the given repository id
     * @since 0.0.1
     */
    @POST("staging/profiles/{profileId}/drop")
    fun dropRepository(
        @Path("profileId") profileId: String,
        @Body promoteRequest: NexusPromoteRequest
    ): Call<Unit>

    /**
     * Fetch the details about the given [repositoryIdKey]
     * @since 0.0.1
     */
    @GET("staging/repository/{repositoryIdKey}")
    fun getRepositoryDetails(
        @Path("repositoryIdKey") repositoryIdKey: String
    ): Call<NexusStagingRepositoryData>
}
