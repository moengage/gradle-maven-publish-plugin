package com.moengage.internal.repository.network

import com.moengage.internal.BASE_TAG
import com.moengage.internal.utils.log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer

/**
 * Interceptor to print the network request
 *
 * @author Abhishek Kumar
 * @since 0.1.0
 */
internal class LoggingInterceptor : Interceptor {

    private val tag = "${BASE_TAG}_LoggingInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        log(message = "$tag intercept(): -------- Network Request ------------")
        log(message = "$tag intercept(): Url - ${chain.request().url()}")
        log(message = "$tag intercept(): Method - ${chain.request().method()}")
        log(message = "$tag intercept(): Header - ${chain.request().headers()}")
        log(message = "$tag intercept(): Body - ${getRequestBodyTextFromRequest(chain.request())}")

        val response = chain.proceed(chain.request())

        log(message = "$tag intercept(): -------- Network Response ------------")
        log(message = "$tag intercept(): Status - ${response.isSuccessful}")
        log(message = "$tag intercept(): Response Code - ${response.code()}")
        log(message = "$tag intercept(): Headers - ${response.headers()}}")
        log(message = "$tag intercept(): Message - ${response.message()}")

        return response
    }

    private fun getRequestBodyTextFromRequest(request: Request): String {
        return try {
            val buffer = Buffer()
            request.newBuilder().build().body()?.writeTo(buffer)
            buffer.readUtf8()
        } catch (t: Throwable) {
            "Failed to parse request body!!"
        }
    }
}
