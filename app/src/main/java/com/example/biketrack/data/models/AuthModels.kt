package com.example.biketrack.data.models

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)

data class RegisterRequest(
    val username: String,
    val name: String,
    val surname: String?,
    val email: String,
    val password: String
) 