package com.arrterm.data.remote.radarr

import retrofit2.http.GET

interface RadarrApi {
    @GET("api/v3/movie")
    suspend fun getMovies(): List<RadarrMovie>

    @GET("api/v3/queue")
    suspend fun getQueue(): RadarrQueueResponse

    @GET("api/v3/system/status")
    suspend fun getSystemStatus(): RadarrSystemStatus
}
