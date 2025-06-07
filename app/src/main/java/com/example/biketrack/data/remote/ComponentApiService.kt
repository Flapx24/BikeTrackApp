package com.example.biketrack.data.remote

import com.example.biketrack.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ComponentApiService {
    
    @POST("components/bicycle/{bicycleId}")
    suspend fun createComponent(
        @Header("Authorization") token: String,
        @Path("bicycleId") bicycleId: Long,
        @Body request: CreateComponentRequest
    ): Response<ApiResponse<BicycleComponentDto>>
    
    @PUT("components/{componentId}")
    suspend fun updateComponent(
        @Header("Authorization") token: String,
        @Path("componentId") componentId: Long,
        @Body request: UpdateComponentRequest
    ): Response<ApiResponse<BicycleComponentDto>>
    
    @DELETE("components/{componentId}")
    suspend fun deleteComponent(
        @Header("Authorization") token: String,
        @Path("componentId") componentId: Long
    ): Response<ApiResponse<Nothing>>
    
    @GET("components/{componentId}")
    suspend fun getComponentById(
        @Header("Authorization") token: String,
        @Path("componentId") componentId: Long
    ): Response<ApiResponse<BicycleComponentDto>>
    
    @GET("components/bicycle/{bicycleId}")
    suspend fun getAllComponentsForBicycle(
        @Header("Authorization") token: String,
        @Path("bicycleId") bicycleId: Long
    ): Response<ApiResponse<List<BicycleComponentDto>>>
    
    @POST("components/bicycle/{bicycleId}/reset")
    suspend fun resetComponentKilometers(
        @Header("Authorization") token: String,
        @Path("bicycleId") bicycleId: Long
    ): Response<ApiResponse<Nothing>>
} 