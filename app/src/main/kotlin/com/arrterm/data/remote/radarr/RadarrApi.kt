package com.arrterm.data.remote.radarr

import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RadarrApi {
    @GET("api/v3/movie")
    suspend fun getMovies(): List<RadarrMovie>

    @GET("api/v3/movie/{id}")
    suspend fun getMovieRaw(@Path("id") id: Int): JsonObject

    @PUT("api/v3/movie/{id}")
    suspend fun updateMovieRaw(@Path("id") id: Int, @Body body: JsonObject): JsonObject

    @DELETE("api/v3/movie/{id}")
    suspend fun deleteMovie(
        @Path("id") id: Int,
        @Query("deleteFiles") deleteFiles: Boolean = false,
        @Query("addImportExclusion") addImportExclusion: Boolean = false,
    )

    @POST("api/v3/command")
    suspend fun postCommand(@Body body: RadarrSearchCommand)

    @GET("api/v3/queue")
    suspend fun getQueue(): RadarrQueueResponse

    @GET("api/v3/system/status")
    suspend fun getSystemStatus(): RadarrSystemStatus
}
