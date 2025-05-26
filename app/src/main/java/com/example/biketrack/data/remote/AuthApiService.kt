package com.example.biketrack.data.remote

import com.example.biketrack.data.models.ApiResponse
import com.example.biketrack.data.models.LoginRequest
import com.example.biketrack.data.models.LoginResponse
import com.example.biketrack.data.models.RegisterRequest
import com.example.biketrack.data.models.RegisterResponse
import com.example.biketrack.data.models.TokenValidationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    @POST("auth/token-login")
    suspend fun tokenLogin(@Header("Authorization") token: String): Response<TokenValidationResponse>
    
    @POST("auth/validate-token")
    suspend fun validateToken(@Header("Authorization") token: String): Response<ApiResponse<Nothing>>
} 