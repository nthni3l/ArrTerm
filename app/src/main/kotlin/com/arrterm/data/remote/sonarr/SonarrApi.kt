package com.arrterm.data.remote.sonarr

import retrofit2.http.GET

interface SonarrApi {
    @GET("api/v3/series")
    suspend fun getSeries(): List<SonarrSeries>

    @GET("api/v3/queue")
    suspend fun getQueue(): SonarrQueueResponse

    @GET("api/v3/system/status")
    suspend fun getSystemStatus(): SonarrSystemStatus
}
