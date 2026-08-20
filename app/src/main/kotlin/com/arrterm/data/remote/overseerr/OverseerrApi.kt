package com.arrterm.data.remote.overseerr

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OverseerrApi {
    @GET("api/v1/request")
    suspend fun getRequests(
        @Query("filter") filter: String = "pending",
        @Query("take") take: Int = 50,
        @Query("sort") sort: String = "added",
    ): OverseerrRequestPage

    @POST("api/v1/request/{requestId}/approve")
    suspend fun approveRequest(@Path("requestId") requestId: Int)

    @POST("api/v1/request/{requestId}/decline")
    suspend fun declineRequest(@Path("requestId") requestId: Int)

    @GET("api/v1/status")
    suspend fun getStatus(): OverseerrStatus
}
