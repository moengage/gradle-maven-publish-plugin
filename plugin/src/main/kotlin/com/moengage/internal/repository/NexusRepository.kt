package com.moengage.internal.repository

import com.moengage.internal.exception.NetworkCallException
import com.moengage.internal.model.NexusPromoteRequest
import com.moengage.internal.model.NexusPromoteRequestData
import com.moengage.internal.model.NexusRepositoryCreateRequest
import com.moengage.internal.model.NexusRepositoryCreateRequestData
import com.moengage.internal.model.NexusStagingRepositoryData
import com.moengage.internal.repository.network.NexusService

/**
 * Repository class for OSSPortal, uses [NexusService] for network calls
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
internal class NexusRepository(
    private val service: NexusService,
    private val profileId: String
) {
    /**
     * Create a new staged repository with the provided description.
     *
     * @return the staged repository identifier
     * @since 1.0.0
     */
    fun createRepository(description: String): String? {
        val createRepositoryResponse =
            service.createRepository(profileId, NexusRepositoryCreateRequest(NexusRepositoryCreateRequestData(description)))
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
        if (!response.isSuccessful) {
            println("::info::no open repository with id $repositoryId or some network error")
            return null
        }
        return response.body()
    }
}
