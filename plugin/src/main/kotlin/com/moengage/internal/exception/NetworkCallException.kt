package com.moengage.internal.exception

/**
 * Exception thrown when some network operation failed or the server responded with an error code
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
class NetworkCallException : Exception {

    /**
     * Constructor with default exception message
     */
    constructor() : super("Network Call Failed")

    /**
     * Constructor with provided exception message
     */
    constructor(detailMessage: String?) : super(detailMessage)

    /**
     * Constructor with provided exception throwable object
     */
    constructor(throwable: Throwable?) : super(throwable)

    /**
     * Constructor with provided exception message and throwable object
     */
    constructor(detailMessage: String?, throwable: Throwable?) : super(detailMessage, throwable)
}
