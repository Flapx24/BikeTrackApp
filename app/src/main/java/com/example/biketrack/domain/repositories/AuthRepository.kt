package com.example.biketrack.domain.repositories

import com.example.biketrack.data.local.UserSession
import com.example.biketrack.data.models.LoginRequest
import com.example.biketrack.data.models.RegisterRequest

sealed class AuthResult<T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error<T>(val message: String) : AuthResult<T>()
}

interface AuthRepository {
    suspend fun login(request: LoginRequest): AuthResult<UserSession>
    suspend fun register(request: RegisterRequest): AuthResult<String>
    suspend fun tokenLogin(token: String): AuthResult<UserSession>
    suspend fun logout(): AuthResult<Unit>
    suspend fun saveUserSession(userSession: UserSession): Boolean
    suspend fun getUserSession(): UserSession?
    suspend fun clearUserSession(): Boolean
    suspend fun hasStoredSession(): Boolean
} 