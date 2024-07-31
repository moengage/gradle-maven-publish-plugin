package com.moengage.internal.utils

internal object LoggerConfiguration {

    var configuredLogLevelValue = LogLevel.NOTICE.value
}

/**
 * Different supported log levels
 * @since 1.0.0
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
 * @since 1.0.0
 */
internal fun log(logLevel: LogLevel = LogLevel.VERBOSE, message: String) {
    val logTag = when (logLevel) {
        LogLevel.VERBOSE -> "::verbose::"
        LogLevel.NOTICE -> "::notice::"
        LogLevel.WARNING -> "::warning::"
        LogLevel.ERROR -> "::error::"
        LogLevel.NO_LOG -> ""
    }

    if (logLevel.value <= LoggerConfiguration.configuredLogLevelValue) {
        println("${logTag}$message")
    }
}
