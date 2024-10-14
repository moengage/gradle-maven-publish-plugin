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
import java.net.SocketTimeoutException

private const val FAILURE_CODE_NOT_FOUND = 404

/**
 * Repository class for OSSPortal, uses [NexusService] for network calls
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
internal class NexusRepository(
    private val service: NexusService,
    private val profileId: String,
    private val maxRetryCountOnTimeOut: Int
) {

    private val tag = "${BASE_TAG}_NexusRepository"

    /**
     * Create a new staged repository with the provided description.
     *
     * @return the staged repository identifier
     * @since 0.0.1
     */
    fun createRepository(description: String, retryCount: Int = 0): String? {
        return try {
            val createRepositoryResponse =
                service.createRepository(profileId, NexusRepositoryCreateRequest(NexusRepositoryCreateRequestData(description)))
                    .execute()
            if (!createRepositoryResponse.isSuccessful) {
                val failureCode = createRepositoryResponse.code()
                val failureMessage = createRepositoryResponse.message()

                log(LogLevel.ERROR, "$tag createRepository(): Failure Code: $failureCode, Failure Message: $failureMessage")
                throw NetworkCallException("Failed to created $description due to response code: $failureCode and response message: $failureMessage")
            }
            createRepositoryResponse.body()?.data?.stagedRepositoryId
        } catch (timeoutException: SocketTimeoutException) {
            retryIfRequired(retryCount) {
                createRepository(description, retryCount + 1)
            }
        }
    }

    /**
     * Close the open staged repository for the given repository id
     * @since 0.0.1
     */
    fun closeRepository(repositoryId: String, retryCount: Int = 0) {
        try {
            val closeRepositoryResponse =
                service.closeRepository(profileId, NexusPromoteRequest(NexusPromoteRequestData(repositoryId))).execute()
            if (!closeRepositoryResponse.isSuccessful) {
                val failureCode = closeRepositoryResponse.code()
                val failureMessage = closeRepositoryResponse.message()

                log(LogLevel.ERROR, "$tag closeRepository(): Failure Code: $failureCode, Failure Message: $failureMessage")
                throw NetworkCallException("Failed to close $repositoryId due to response code: $failureCode and response message: $failureMessage")
            }
        } catch (socketTimeoutException: SocketTimeoutException) {
            retryIfRequired(retryCount) {
                closeRepository(repositoryId, retryCount + 1)
            }
        }
    }

    /**
     * Move the staged repository to next state for the given repository id
     * @since 0.0.1
     */
    fun promoteRepository(repositoryId: String, retryCount: Int = 0) {
        try {
            val promoteResponse =
                service.promoteRepository(profileId, NexusPromoteRequest(NexusPromoteRequestData(repositoryId))).execute()
            if (!promoteResponse.isSuccessful) {
                val failureCode = promoteResponse.code()
                val failureMessage = promoteResponse.message()

                log(LogLevel.ERROR, "$tag promoteRepository(): Failure Code: $failureCode, Failure Message: $failureMessage")
                throw NetworkCallException("Failed to promote $repositoryId due to response code: $failureCode and response message: $failureMessage")
            }
        } catch (socketTimeoutException: SocketTimeoutException) {
            retryIfRequired(retryCount) {
                promoteRepository(repositoryId, retryCount + 1)
            }
        }
    }

    /**
     * Drop the staged repository for the given repository id
     * @since 0.0.1
     */
    fun dropRepository(repositoryId: String, retryCount: Int = 0) {
        try {
            val dropResponse =
                service.dropRepository(profileId, NexusPromoteRequest(NexusPromoteRequestData(repositoryId))).execute()
            if (!dropResponse.isSuccessful) {
                val failureCode = dropResponse.code()
                val failureMessage = dropResponse.message()

                log(LogLevel.ERROR, "$tag dropRepository(): Failure Code: $failureCode, Failure Message: $failureMessage")
                throw NetworkCallException("Failed to drop $repositoryId due to response code: $failureCode and response message: $failureMessage")
            }
        } catch (socketTimeoutException: SocketTimeoutException) {
            retryIfRequired(retryCount) {
                dropRepository(repositoryId, retryCount + 1)
            }
        }
    }

    fun getRepositoryData(repositoryId: String, retryCount: Int = 0): NexusStagingRepositoryData? {
        return try {
            val response = service.getRepositoryDetails(repositoryId).execute()
            if (!response.isSuccessful) {
                val failureCode = response.code()
                val failureMessage = response.message()
                if (failureCode == FAILURE_CODE_NOT_FOUND) {
                    log(message = "$tag getRepositoryData(): no open repository found for $repositoryId")
                    return null
                }

                log(LogLevel.ERROR, "$tag getRepositoryData(): Failure Code: $failureCode, Failure Message: $failureMessage")
                throw NetworkCallException("Failed to get data for $repositoryId due to response code: $failureCode and response message: $failureMessage")
            }
            response.body()
        } catch (socketTimeoutException: SocketTimeoutException) {
            retryIfRequired(retryCount) {
                getRepositoryData(repositoryId, retryCount + 1)
            }
        }
    }

    private fun <T> retryIfRequired(retryCount: Int, retryFunction: () -> T?): T? {
        log(LogLevel.ERROR, "$tag retryIfRequired(): Timeout, retryCount = $retryCount, maxRetryCount = $maxRetryCountOnTimeOut")
        return if (retryCount < maxRetryCountOnTimeOut) {
            log(LogLevel.NOTICE, "$tag retryIfRequired(): Retrying ...")
            retryFunction()
        } else {
            log(LogLevel.WARNING, "$tag retryIfRequired(): Retry limit reached")
            null
        }
    }
}
