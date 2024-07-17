package com.moengage.internal.repository.network

import com.moengage.internal.model.MavenCentralPortal
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Base64

private const val authorizationHeader = "Authorization"
private const val userAgentHeader = "User-Agent"
private const val acceptedEncodingHeader = "Accept-Encoding"
private const val pluginAgentIdentifier = "moengage-release-plugin"

internal class AuthorizationInterceptor(
    private val mavenCentralPortal: MavenCentralPortal,
    private val username: String,
    private val password: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().apply {
                addHeader(acceptedEncodingHeader, "application/json")
                addHeader(userAgentHeader, pluginAgentIdentifier)
                if (mavenCentralPortal != MavenCentralPortal.CENTRAL_PORTAL) {
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
