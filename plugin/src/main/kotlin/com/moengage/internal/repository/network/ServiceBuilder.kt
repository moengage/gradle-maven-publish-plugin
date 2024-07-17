package com.moengage.internal.repository.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.moengage.internal.model.MavenCentralPortal
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private const val timeout = 10L

internal class ServiceBuilder(
    private val mavenCentralPortal: MavenCentralPortal,
    private val username: String,
    private val password: String
) {

    private val retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(mavenCentralPortal, username, password))
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .client(okHttpClient)
            .baseUrl(mavenCentralPortal.host)
            .build()
    }

    private val nexusService by lazy {
        retrofit.create(NexusService::class.java)
    }

    private val centerPortalService by lazy {
        retrofit.create(CentralPortalService::class.java)
    }

    fun getNexusService(): NexusService = nexusService

    fun getCentralPortalService(): CentralPortalService = centerPortalService
}
