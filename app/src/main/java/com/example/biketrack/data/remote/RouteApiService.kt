package com.example.biketrack.data.remote

import com.example.biketrack.data.models.*
import retrofit2.Response
import retrofit2.http.*

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
    
    @GET("routes/{routeId}")
    suspend fun getRouteById(
        @Header("Authorization") token: String,
        @Path("routeId") routeId: Long
    ): Response<ApiResponse<RouteDto>>
    
    @GET("reviews/route/{routeId}")
    suspend fun getRouteReviews(
        @Header("Authorization") token: String,
        @Path("routeId") routeId: Long,
        @Query("lastReviewId") lastReviewId: Long? = null
    ): Response<ApiResponse<List<ReviewDto>>>
    
    @GET("reviews/route/{routeId}/mine")
    suspend fun getCurrentUserReview(
        @Header("Authorization") token: String,
        @Path("routeId") routeId: Long
    ): Response<ApiResponse<ReviewDto>>
    
    @POST("reviews/route/{routeId}")
    suspend fun createReview(
        @Header("Authorization") token: String,
        @Path("routeId") routeId: Long,
        @Body request: CreateReviewRequest
    ): Response<ApiResponse<ReviewDto>>
    
    @PUT("reviews/route/{routeId}")
    suspend fun updateReview(
        @Header("Authorization") token: String,
        @Path("routeId") routeId: Long,
        @Body request: UpdateReviewRequest
    ): Response<ApiResponse<ReviewDto>>
    
    @DELETE("reviews/route/{routeId}")
    suspend fun deleteReview(
        @Header("Authorization") token: String,
        @Path("routeId") routeId: Long
    ): Response<ApiResponse<Nothing>>
    
    @GET("route-updates/route/{routeId}")
    suspend fun getRouteUpdates(
        @Header("Authorization") token: String,
        @Path("routeId") routeId: Long
    ): Response<ApiResponse<List<RouteUpdateDto>>>
    
    @POST("route-updates")
    suspend fun createRouteUpdate(
        @Header("Authorization") token: String,
        @Body request: CreateRouteUpdateRequest
    ): Response<ApiResponse<RouteUpdateDto>>
    
    @PUT("route-updates")
    suspend fun updateRouteUpdate(
        @Header("Authorization") token: String,
        @Body request: UpdateRouteUpdateRequest
    ): Response<ApiResponse<RouteUpdateDto>>
    
    @DELETE("route-updates/{routeUpdateId}")
    suspend fun deleteRouteUpdate(
        @Header("Authorization") token: String,
        @Path("routeUpdateId") routeUpdateId: Long
    ): Response<ApiResponse<Nothing>>
} 