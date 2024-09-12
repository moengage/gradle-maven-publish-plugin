package com.moengage.internal.model

/**
 * Different states for the staged nexus oss portal repository state
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
internal enum class NexusArtifactTransitionState {

    /**
     * Status "open", next possible state "released" / "closed"
     */
    OPEN,

    /**
     * Status "released", next possible state "closed"
     */
    RELEASED,

    /**
     * Status "closed"
     */
    CLOSED
}
