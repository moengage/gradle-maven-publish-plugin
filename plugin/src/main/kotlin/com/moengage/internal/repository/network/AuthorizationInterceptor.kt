package com.moengage.internal.repository.network

import com.moengage.internal.model.ArtifactReleasePortal
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Base64

private const val authorizationHeader = "Authorization"
private const val userAgentHeader = "User-Agent"
private const val acceptedEncodingHeader = "Accept"
private const val pluginAgentIdentifier = "moengage-release-plugin"

/**
 * OktHttps interceptor to add the required authorization header in the request
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
internal class AuthorizationInterceptor(
    private val artifactReleasePortal: ArtifactReleasePortal,
    private val username: String,
    private val password: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                addHeader(acceptedEncodingHeader, "application/json")
                addHeader(userAgentHeader, pluginAgentIdentifier)
                if (artifactReleasePortal != ArtifactReleasePortal.CENTRAL_PORTAL) {
                    addHeader(authorizationHeader, Credentials.basic(username, password))
                } else {
                    val bearerToken =
                        Base64.getEncoder().encode("$username:$password".toByteArray()).toString(Charsets.UTF_8)
                    addHeader(authorizationHeader, "Bearer $bearerToken")
                }
            }.build()
        )
    }
}
