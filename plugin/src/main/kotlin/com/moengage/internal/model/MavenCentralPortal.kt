package com.moengage.internal.model

import com.moengage.internal.CENTRAL_PORTAL_HOST
import com.moengage.internal.OSS_PORTAL_HOST
import com.moengage.internal.S01_OSS_PORTAL_HOST

/**
 * Different supported portal to upload the artifacts to maven center
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
internal enum class MavenCentralPortal(internal val host: String) {

    /**
     * oss portal with host [OSS_PORTAL_HOST]
     */
    OSS_PORTAL(OSS_PORTAL_HOST),

    /**
     * s01 oss portal with host [S01_OSS_PORTAL_HOST]
     */
    S01_OSS_PORTAL(S01_OSS_PORTAL_HOST),

    /**
     * new maven central portal with host [CENTRAL_PORTAL_HOST]
     */
    CENTRAL_PORTAL(CENTRAL_PORTAL_HOST)
}
