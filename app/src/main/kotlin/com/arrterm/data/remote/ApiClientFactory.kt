package com.arrterm.data.remote

import com.arrterm.data.settings.ServerConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Builds a Retrofit instance per ServerConfig on demand, since the base URL/API key are
 * user-editable at runtime rather than fixed at app startup. Instances are cached by
 * config so repeated calls with an unchanged config don't rebuild the client each time.
 */
object ApiClientFactory {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    private val cache = ConcurrentHashMap<String, Retrofit>()

    inline fun <reified T> create(config: ServerConfig): T =
        retrofitFor(config).create(T::class.java)

    fun retrofitFor(config: ServerConfig): Retrofit {
        val cacheKey = "${config.normalizedBaseUrl}|${config.apiKey}"
        return cache.getOrPut(cacheKey) { buildRetrofit(config) }
    }

    fun invalidate(config: ServerConfig) {
        cache.remove("${config.normalizedBaseUrl}|${config.apiKey}")
    }

    private fun buildRetrofit(config: ServerConfig): Retrofit {
        val apiKeyInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-Api-Key", config.apiKey)
                .build()
            chain.proceed(request)
        }
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("${config.normalizedBaseUrl}/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
