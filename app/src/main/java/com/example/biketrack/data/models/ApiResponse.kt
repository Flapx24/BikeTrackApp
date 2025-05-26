package com.example.biketrack.data.models

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

data class LoginResponse(
    val success: Boolean,
    val id: Long,
    val token: String,
    val name: String,
    val message: String
)

data class RegisterResponse(
    val success: Boolean,
    val id: Long,
    val name: String,
    val message: String,
    val imageUrl: String?
)

data class TokenValidationResponse(
    val success: Boolean,
    val id: Long?,
    val name: String?,
    val message: String
) 