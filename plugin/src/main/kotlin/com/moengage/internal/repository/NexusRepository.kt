package com.moengage.internal.repository

import com.moengage.internal.BASE_TAG
import com.moengage.internal.exception.NetworkCallException
import com.moengage.internal.model.NexusPromoteRequest
import com.moengage.internal.model.NexusPromoteRequestData
import com.moengage.internal.model.NexusRepositoryCreateRequest
import com.moengage.internal.model.NexusRepositoryCreateRequestData
import com.moengage.internal.model.NexusStagingRepositoryData
import com.moengage.internal.repository.network.NexusService
import com.moengage.internal.utils.LogLevel
import com.moengage.internal.utils.log

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

    private val tag = "${BASE_TAG}_NexusRepository"

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
            log(LogLevel.ERROR, "$tag createRepository(): ${createRepositoryResponse.errorBody()}")
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
            log(LogLevel.ERROR, "$tag closeRepository(): ${closeRepositoryResponse.errorBody()}")
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
            log(LogLevel.ERROR, "$tag promoteRepository(): ${promoteResponse.errorBody()}")
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
            log(LogLevel.ERROR, "$tag dropRepository(): ${dropResponse.errorBody()}")
            throw NetworkCallException("Failed to drop staging repository with id - $repositoryId")
        }
    }

    fun getRepositoryData(repositoryId: String): NexusStagingRepositoryData? {
        val response = service.getRepositoryDetails(repositoryId).execute()
        if (!response.isSuccessful) {
            log(LogLevel.ERROR, "$tag getRepositoryData(): ${response.errorBody()}")
            return null
        }
        return response.body()
    }
}
