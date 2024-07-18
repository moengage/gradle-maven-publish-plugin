package com.moengage.internal.model

/**
 * Different states for the staged nexus oss portal repository state
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
internal enum class NexusTransitionState(val state: String) {

    OPEN("open"),

    RELEASED("released"),

    OTHER("other")
}