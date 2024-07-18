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

/**
 * Interface containing all different network requests used by oss nexus portal
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
internal interface NexusService {

    /**
     * Return the list of staged repositories with the details
     * @since 1.0.0
     */
    @GET("staging/profile_repositories/{profileId}")
    fun getStagedRepositories(
        @Path("profileId") profileId: String
    ): Call<NexusStagingRepository>

    /**
     * Create a new staged repository with the required details [NexusArtifactCreateRequest]
     * @since 1.0.0
     */
    @POST("staging/profile_repositories/{profileId}/start")
    fun createRepository(
        @Path("profileId") profileId: String,
        @Body createRequest: NexusArtifactCreateRequest
    ): Call<NexusArtifactCreateResponse>

    /**
     * Close the open staged repository for the given repository id
     * @since 1.0.0
     */
    @POST("staging/profile_repositories/{profileId}/finish")
    fun closeRepository(
        @Path("profileId") profileId: String,
        @Body promoteRequest: NexusPromoteRequest
    ): Call<Unit>

    /**
     * Move the staged repository to next state for the given repository id
     * @since 1.0.0
     */
    @POST("staging/profile_repositories/{profileId}/promote")
    fun promoteRepository(
        @Path("profileId") profileId: String,
        @Body promoteRequest: NexusPromoteRequest
    ): Call<Unit>

    /**
     * Drop the staged repository for the given repository id
     * @since 1.0.0
     */
    @POST("staging/profile_repositories/{profileId}/drop")
    fun dropRepository(
        @Path("profileId") profileId: String,
        @Body promoteRequest: NexusPromoteRequest
    ): Call<Unit>
}
