package com.moengage.internal.model

import kotlinx.serialization.Serializable

/**
 * Info about a specific staging repository
 * @since 1.0.0
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
 * Info about the staged repositories
 * @since 1.0.0
 */
@Serializable
internal data class NexusStagingRepository(val data: List<NexusStagingRepositoryData>)

/**
 * Request class for the creating staging repository
 * @since 1.0.0
 */
@Serializable
internal data class NexusArtifactCreateRequestData(val description: String)

/**
 * Data for the creating staging repository
 * @since 1.0.0
 */
@Serializable
internal data class NexusArtifactCreateRequest(val data: NexusArtifactCreateRequestData)

/**
 * Response class while creating staging repository
 * @since 1.0.0
 */
@Serializable
internal data class NexusArtifactCreateResponseData(val stagedRepositoryId: String)

/**
 * Data response while creating staging repository
 * @since 1.0.0
 */
@Serializable
internal data class NexusArtifactCreateResponse(val data: NexusArtifactCreateResponseData)

/**
 * Request for changing the state of staged repository
 * @since 1.0.0
 */
@Serializable
internal data class NexusPromoteRequest(val data: NexusPromoteRequestData)

/**
 * Request data for changing the state of staged repository
 * @since 1.0.0
 */
@Serializable
internal data class NexusPromoteRequestData(val stagedRepositoryId: String)
