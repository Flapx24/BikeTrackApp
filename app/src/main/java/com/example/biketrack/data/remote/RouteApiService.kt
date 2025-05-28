package com.example.biketrack.data.remote

import com.example.biketrack.data.models.ApiResponse
import com.example.biketrack.data.models.RouteSummaryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RouteApiService {
    
    @GET("routes")
    suspend fun getAllRoutes(
        @Header("Authorization") token: String,
        @Query("lastRouteId") lastRouteId: Long? = null
    ): Response<ApiResponse<List<RouteSummaryDto>>>
    
    @GET("routes/filter")
    suspend fun filterRoutes(
        @Header("Authorization") token: String,
        @Query("city") city: String,
        @Query("minScore") minScore: Int? = null,
        @Query("lastRouteId") lastRouteId: Long? = null
    ): Response<ApiResponse<List<RouteSummaryDto>>>
    
    @GET("routes/filter")
    suspend fun filterRoutesByRating(
        @Header("Authorization") token: String,
        @Query("minScore") minScore: Int,
        @Query("lastRouteId") lastRouteId: Long? = null
    ): Response<ApiResponse<List<RouteSummaryDto>>>
} 