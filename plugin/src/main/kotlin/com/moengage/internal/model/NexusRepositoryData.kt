package com.moengage.internal.model

import kotlinx.serialization.Serializable

@Serializable
internal data class NexusStagingRepository(val data: NexusStagingRepositoryData)

@Serializable
internal data class NexusStagingRepositoryData(
    val profileId: String,
    val repositoryId: String,
    val type: String,
    val repositoryURI: String,
    val description: String,
    val transitioning: Boolean
)

@Serializable
internal data class NexusArtifactCreateRequestData(val description: String)

@Serializable
internal data class NexusArtifactCreateRequest(val data: NexusArtifactCreateRequestData)

@Serializable
internal data class NexusArtifactCreateResponseData(val stagedRepositoryId: String)

@Serializable
internal data class NexusArtifactCreateResponse(val data: NexusArtifactCreateResponseData)

@Serializable
internal data class NexusPromoteRequest(val data: NexusPromoteRequestData)

@Serializable
internal data class NexusPromoteRequestData(val stagedRepositoryId: String)
