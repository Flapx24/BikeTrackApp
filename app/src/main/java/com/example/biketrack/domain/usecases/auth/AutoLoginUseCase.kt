package com.example.biketrack.domain.usecases.auth

import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.data.local.UserSession
import com.example.biketrack.domain.repositories.AuthRepository
import com.example.biketrack.domain.repositories.AuthResult

class AutoLoginUseCase(private val authRepository: AuthRepository) {
    
    suspend operator fun invoke(): AuthResult<UserSession> {
        // Check if there's a stored session (only exists if "Keep session" was checked)
        if (!authRepository.hasStoredSession()) {
            return AuthResult.Error("No stored session - user must login manually")
        }
        
        val storedSession = authRepository.getUserSession()
        if (storedSession == null) {
            return AuthResult.Error("Error retrieving stored session")
        }
        
        // Check if token is still valid with API
        val result = authRepository.tokenLogin(storedSession.token)
        
        if (result is AuthResult.Success) {
            val userSession = result.data
            // Update in-memory session with possibly new data
            SessionManager.setUserSession(userSession)
            // Update secure storage with updated data
            authRepository.saveUserSession(userSession)
        } else {
            // If token is not valid, clear stored data
            authRepository.clearUserSession()
            SessionManager.clearSession()
        }
        
        return result
    }
} 