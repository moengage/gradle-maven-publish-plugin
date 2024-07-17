package com.moengage.internal.model

import com.moengage.internal.CENTRAL_PORTAL_HOST
import com.moengage.internal.OSS_PORTAL_HOST
import com.moengage.internal.S01_OSS_PORTAL_HOST

internal enum class MavenCentralPortal(internal val host: String) {

    OSS_PORTAL(OSS_PORTAL_HOST),

    S01_OSS_PORTAL(S01_OSS_PORTAL_HOST),

    CENTRAL_PORTAL(CENTRAL_PORTAL_HOST)
}
