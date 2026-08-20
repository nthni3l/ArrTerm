package com.arrterm.data.remote.sonarr

import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SonarrApi {
    @GET("api/v3/series")
    suspend fun getSeries(): List<SonarrSeries>

    @GET("api/v3/series/{id}")
    suspend fun getSeriesRaw(@Path("id") id: Int): JsonObject

    @PUT("api/v3/series/{id}")
    suspend fun updateSeriesRaw(@Path("id") id: Int, @Body body: JsonObject): JsonObject

    @DELETE("api/v3/series/{id}")
    suspend fun deleteSeries(
        @Path("id") id: Int,
        @Query("deleteFiles") deleteFiles: Boolean = false,
        @Query("addImportListExclusion") addImportListExclusion: Boolean = false,
    )

    @POST("api/v3/command")
    suspend fun postCommand(@Body body: SonarrSearchCommand)

    @GET("api/v3/queue")
    suspend fun getQueue(): SonarrQueueResponse

    @GET("api/v3/system/status")
    suspend fun getSystemStatus(): SonarrSystemStatus
}
