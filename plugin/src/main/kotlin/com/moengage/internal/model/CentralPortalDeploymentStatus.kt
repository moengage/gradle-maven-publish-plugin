package com.moengage.internal.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CentralPortalDeploymentStatus(
    val deploymentId: String,
    val deploymentName: String,
    val deploymentState: String,
    val purls: List<String>,
    val errors: List<String>
)
