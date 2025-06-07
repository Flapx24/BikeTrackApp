package com.example.biketrack.data.remote

import com.example.biketrack.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface BicycleApiService {
    
    @GET("bicycles")
    suspend fun getAllUserBicycles(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<BicycleSummaryDto>>>
    
    @GET("bicycles/{bicycleId}")
    suspend fun getBicycleById(
        @Header("Authorization") token: String,
        @Path("bicycleId") bicycleId: Long
    ): Response<ApiResponse<BicycleDto>>
    
    @POST("bicycles")
    suspend fun createBicycle(
        @Header("Authorization") token: String,
        @Body request: CreateBicycleRequest
    ): Response<ApiResponse<BicycleDto>>
    
    @PUT("bicycles/{bicycleId}")
    suspend fun updateBicycle(
        @Header("Authorization") token: String,
        @Path("bicycleId") bicycleId: Long,
        @Body request: UpdateBicycleRequest
    ): Response<ApiResponse<BicycleDto>>
    
    @DELETE("bicycles/{bicycleId}")
    suspend fun deleteBicycle(
        @Header("Authorization") token: String,
        @Path("bicycleId") bicycleId: Long
    ): Response<ApiResponse<Nothing>>
    
    @POST("bicycles/{bicycleId}/add-kilometers")
    suspend fun addKilometers(
        @Header("Authorization") token: String,
        @Path("bicycleId") bicycleId: Long,
        @Query("kilometers") kilometers: Double
    ): Response<ApiResponse<BicycleDto>>
    
    @POST("bicycles/{bicycleId}/subtract-kilometers")
    suspend fun subtractKilometers(
        @Header("Authorization") token: String,
        @Path("bicycleId") bicycleId: Long,
        @Query("kilometers") kilometers: Double
    ): Response<ApiResponse<BicycleDto>>
} 