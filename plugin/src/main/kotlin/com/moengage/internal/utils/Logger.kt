package com.moengage.internal.utils

/**
 * Different supported log levels
 * @since 1.0.0
 */
internal enum class LogLevel {

    INFO,

    NOTICE,

    WARNING,

    ERROR
}

/**
 * Logger function to handle the logging message with appropriate level
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
internal fun log(logLevel: LogLevel = LogLevel.INFO, message: String) {
    val logTag = when (logLevel) {
        LogLevel.INFO -> ""
        LogLevel.NOTICE -> "::notice::"
        LogLevel.WARNING -> "::warning::"
        LogLevel.ERROR -> "::error::"
    }

    println("${logTag}$message")
}
