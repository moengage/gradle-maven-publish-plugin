package com.moengage.internal.model

import com.moengage.internal.CENTRAL_PORTAL_HOST
import com.moengage.internal.CENTRAL_PORTAL_STAGING_HOST
import com.moengage.internal.OSS_PORTAL_HOST
import com.moengage.internal.OSS_PORTAL_STAGING_HOST
import com.moengage.internal.S01_OSS_PORTAL_HOST
import com.moengage.internal.S01_OSS_PORTAL_STAGING_HOST

/**
 * Different supported portal to upload the artifacts to maven center
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
internal enum class ArtifactReleasePortal(internal val baseHostUrl: String, internal val baseStagingHostUrl: String) {

    /**
     * oss portal with host [OSS_PORTAL_HOST]
     */
    OSS_PORTAL(OSS_PORTAL_HOST, OSS_PORTAL_STAGING_HOST),

    /**
     * s01 oss portal with host [S01_OSS_PORTAL_HOST]
     */
    S01_OSS_PORTAL(S01_OSS_PORTAL_HOST, S01_OSS_PORTAL_STAGING_HOST),

    /**
     * new maven central portal with host [CENTRAL_PORTAL_HOST]
     */
    CENTRAL_PORTAL(CENTRAL_PORTAL_HOST, CENTRAL_PORTAL_STAGING_HOST);

    companion object {

        /**
         * Return the instance of [ArtifactReleasePortal] from the given host name
         * @since 0.0.1
         */
        fun getMavenCentralPortal(host: String?): ArtifactReleasePortal {
            return when (host) {
                "OSS_PORTAL" -> OSS_PORTAL
                "S01_OSS_PORTAL" -> S01_OSS_PORTAL
                "CENTRAL_PORTAL" -> CENTRAL_PORTAL
                else -> CENTRAL_PORTAL
            }
        }
    }
}
