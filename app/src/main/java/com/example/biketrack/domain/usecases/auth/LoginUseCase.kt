package com.example.biketrack.domain.usecases.auth

import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.data.local.UserSession
import com.example.biketrack.data.models.LoginRequest
import com.example.biketrack.domain.repositories.AuthRepository
import com.example.biketrack.domain.repositories.AuthResult

class LoginUseCase(private val authRepository: AuthRepository) {
    
    suspend operator fun invoke(
        email: String,
        password: String,
        rememberMe: Boolean
    ): AuthResult<UserSession> {
        val request = LoginRequest(email, password, rememberMe)
        val result = authRepository.login(request)
        
        if (result is AuthResult.Success) {
            val userSession = result.data
            // Always store in global memory for current session
            SessionManager.setUserSession(userSession)
            
            if (rememberMe) {
                // Only save to secure storage if "Keep session" is checked
                authRepository.saveUserSession(userSession)
            } else {
                // If "Keep session" is not checked, clear any previously stored data
                // but safely without blocking login
                try {
                    authRepository.clearUserSession()
                } catch (e: Exception) {
                    // If cleanup fails, continue - not critical for successful login
                    e.printStackTrace()
                }
            }
        }
        
        return result
    }
} 