package com.moengage.internal.repository.network

import com.moengage.internal.model.ArtifactReleasePortal
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

internal const val defaultTimeout = 60L // 1 minute
internal const val maximumTimeOutDurationForNetworkCall = 300L // 5 minutes
internal const val maximumRetryCountForNexusRepository = 5

/**
 * Manage the different network service classes
 *
 * @author Abhishek Kumar
 * @since 0.0.1
 */
internal class ServiceBuilder(
    private val artifactReleasePortal: ArtifactReleasePortal,
    private val username: String,
    private val password: String,
    private val networkTimeoutDuration: Long
) {
    private val retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder().apply {
            connectTimeout(networkTimeoutDuration, TimeUnit.SECONDS)
            readTimeout(networkTimeoutDuration, TimeUnit.SECONDS)
            writeTimeout(networkTimeoutDuration, TimeUnit.SECONDS)
            addInterceptor(LoggingInterceptor(artifactReleasePortal))
            addInterceptor(AuthorizationInterceptor(artifactReleasePortal, username, password))
        }.build()

        val json = Json { ignoreUnknownKeys = true }
        val builder = Retrofit.Builder()
        if (artifactReleasePortal == ArtifactReleasePortal.CENTRAL_PORTAL) {
            builder.addConverterFactory(ScalarsConverterFactory.create())
        } else {
            builder.addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        }
        builder.also {
            it.client(okHttpClient)
            it.baseUrl(artifactReleasePortal.baseHostUrl)
        }.build()
    }

    /**
     * Return the [NexusService] instance
     * @since 0.0.1
     */
    fun getNexusService() = retrofit.create(NexusService::class.java)

    /**
     * Return the [CentralPortalService] instance
     * @since 0.0.1
     */
    fun getCentralPortalService() = retrofit.create(CentralPortalService::class.java)
}
