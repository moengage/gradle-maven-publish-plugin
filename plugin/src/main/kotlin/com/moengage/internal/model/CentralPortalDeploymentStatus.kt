package com.moengage.internal.model

import com.moengage.internal.repository.network.CentralPortalService
import kotlinx.serialization.Serializable

/**
 * Data class for the response returned from the [CentralPortalService] while getting repository response
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Serializable
internal data class CentralPortalDeploymentStatus(

    /**
     * Deployment Id which represents unique identifier for the repository
     */
    val deploymentId: String,

    /**
     * Deployment name which was used to upload the repository
     */
    val deploymentName: String,

    /**
     * Current deployment status
     */
    val deploymentState: String,

    /**
     * List of error while processing the deployment (if any error is encountered)
     */
    val errors: List<String>
)
