package com.moengage.internal.repository

import com.moengage.internal.exception.NetworkCallException
import com.moengage.internal.model.NexusArtifactCreateRequest
import com.moengage.internal.model.NexusArtifactCreateRequestData
import com.moengage.internal.model.NexusPromoteRequest
import com.moengage.internal.model.NexusPromoteRequestData
import com.moengage.internal.model.NexusStagingRepositoryData
import com.moengage.internal.repository.network.NexusService

/**
 * Repository class for [OSSPortal], uses [NexusService] for network calls
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
internal class NexusRepository(
    private val service: NexusService,
    private val profileId: String
) {

    /**
     * Return the list of staged repositories with the details
     * @since 1.0.0
     */
    fun getStagedRepositories(): List<NexusStagingRepositoryData> {
        val stagedRepositoriesResponse = service.getStagedRepositories(profileId).execute()
        if (!stagedRepositoriesResponse.isSuccessful) {
            throw NetworkCallException("Failed to get staged repositories.")
        }
        return stagedRepositoriesResponse.body()?.data ?: emptyList()
    }

    /**
     * Create a new staged repository with the provided description.
     *
     * @return the staged repository identifier
     * @since 1.0.0
     */
    fun createRepository(description: String): String? {
        val createRepositoryResponse =
            service.createRepository(profileId, NexusArtifactCreateRequest(NexusArtifactCreateRequestData(description)))
                .execute()
        if (!createRepositoryResponse.isSuccessful) {
            throw NetworkCallException("Failed to create staging repository with description - $description")
        }
        return createRepositoryResponse.body()?.data?.stagedRepositoryId
    }

    /**
     * Close the open staged repository for the given repository id
     * @since 1.0.0
     */
    fun closeRepository(repositoryId: String) {
        val closeRepositoryResponse =
            service.closeRepository(profileId, NexusPromoteRequest(NexusPromoteRequestData(repositoryId))).execute()
        if (!closeRepositoryResponse.isSuccessful) {
            throw NetworkCallException("Failed to close staging repository with id - $repositoryId")
        }
    }

    /**
     * Move the staged repository to next state for the given repository id
     * @since 1.0.0
     */
    fun promoteRepository(repositoryId: String) {
        val promoteResponse =
            service.promoteRepository(profileId, NexusPromoteRequest(NexusPromoteRequestData(repositoryId))).execute()
        if (!promoteResponse.isSuccessful) {
            throw NetworkCallException("Failed to promote staging repository with id - $repositoryId")
        }
    }

    /**
     * Drop the staged repository for the given repository id
     * @since 1.0.0
     */
    fun dropRepository(repositoryId: String) {
        val dropResponse =
            service.dropRepository(profileId, NexusPromoteRequest(NexusPromoteRequestData(repositoryId))).execute()
        if (!dropResponse.isSuccessful) {
            throw NetworkCallException("Failed to drop staging repository with id - $repositoryId")
        }
    }

    fun getRepositoryData(repositoryId: String): NexusStagingRepositoryData? {
        val response = service.getRepositoryDetails(repositoryId).execute()
        if (response.isSuccessful) {
            println("::info::no open repository with id $repositoryId or some network error")
            return null
        }
        return response.body()
    }
}
