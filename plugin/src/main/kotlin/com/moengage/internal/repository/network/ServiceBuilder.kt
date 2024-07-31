package com.moengage.internal.repository.network

import com.moengage.internal.model.ArtifactReleasePortal
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val timeout = 60L

/**
 * Manage the different network service classes
 *
 * @author Abhishek Kumar
 * @since 1.0.0
 */
internal class ServiceBuilder(
    private val artifactReleasePortal: ArtifactReleasePortal,
    private val username: String,
    private val password: String
) {

    private val retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(artifactReleasePortal, username, password))
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()

        val json = Json { ignoreUnknownKeys = true }
        Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
            .client(okHttpClient)
            .baseUrl(artifactReleasePortal.baseHostUrl)
            .build()
    }

    /**
     * Return the [NexusService] instance
     * @since 1.0.0
     */
    fun getNexusService() = retrofit.create(NexusService::class.java)

    /**
     * Return the [CentralPortalService] instance
     * @since 1.0.0
     */
    fun getCentralPortalService() = retrofit.create(CentralPortalService::class.java)
}
