package com.example.biketrack.data.remote

import com.example.biketrack.data.models.ApiResponse
import com.example.biketrack.data.models.WorkshopDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkshopApiService {
    
    @GET("workshops/{workshopId}")
    suspend fun getWorkshopById(
        @Header("Authorization") token: String,
        @Path("workshopId") workshopId: Long
    ): Response<ApiResponse<WorkshopDto>>
    
    @GET("workshops/city")
    suspend fun getWorkshopsByCity(
        @Header("Authorization") token: String,
        @Query("city") city: String
    ): Response<ApiResponse<List<WorkshopDto>>>
} 