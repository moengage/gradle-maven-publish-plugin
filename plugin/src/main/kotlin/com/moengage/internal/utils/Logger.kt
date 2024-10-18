package com.moengage.internal.utils

internal object LoggerConfiguration {

    var configuredLogLevelValue = LogLevel.NOTICE.value
}

/**
 * Different supported log levels
 * @since 0.0.1
 */
internal enum class LogLevel(val value: Int) {

    NO_LOG(0),

    ERROR(1),

    WARNING(2),

    NOTICE(3),

    VERBOSE(4)
}

/**
 * Logger function to handle the logging message with appropriate level
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
internal fun log(logLevel: LogLevel = LogLevel.VERBOSE, message: String) {
    val logTag = when (logLevel) {
        LogLevel.ERROR -> "::error::"
        else -> ""
    }

    if (logLevel.value <= LoggerConfiguration.configuredLogLevelValue) {
        println("${logTag}$message")
    }
}
