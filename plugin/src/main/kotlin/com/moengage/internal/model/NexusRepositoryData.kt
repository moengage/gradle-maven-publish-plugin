package com.moengage.internal.model

import kotlinx.serialization.Serializable

/**
 * Info about a specific staging repository
 * @since 0.0.1
 */
@Serializable
internal data class NexusStagingRepositoryData(

    /**
     * Profile id on which the repository is located
     */
    val profileId: String,

    /**
     * Unique identifier of the staged repository
     */
    val repositoryId: String,

    /**
     * State of the staging repository ["open", "closed", "released"]
     */
    val type: String,

    /**
     * UIR for the staging repository
     */
    val repositoryURI: String,

    /**
     * Description of the staging repository, provided during creation of the staging repository
     */
    val description: String,

    /**
     * True if the repository is in transitioning from one state to another, false otherwise
     */
    val transitioning: Boolean
)

/**
 * Request class for the creating staging repository
 * @since 0.0.1
 */
@Serializable
internal data class NexusRepositoryCreateRequestData(val description: String)

/**
 * Data for the creating staging repository
 * @since 0.0.1
 */
@Serializable
internal data class NexusRepositoryCreateRequest(val data: NexusRepositoryCreateRequestData)

/**
 * Response class while creating staging repository
 * @since 0.0.1
 */
@Serializable
internal data class NexusRepositoryCreateResponseData(val stagedRepositoryId: String)

/**
 * Data response while creating staging repository
 * @since 0.0.1
 */
@Serializable
internal data class NexusRepositoryCreateResponse(val data: NexusRepositoryCreateResponseData)

/**
 * Request for changing the state of staged repository
 * @since 0.0.1
 */
@Serializable
internal data class NexusPromoteRequest(val data: NexusPromoteRequestData)

/**
 * Request data for changing the state of staged repository
 * @since 0.0.1
 */
@Serializable
internal data class NexusPromoteRequestData(val stagedRepositoryId: String)
