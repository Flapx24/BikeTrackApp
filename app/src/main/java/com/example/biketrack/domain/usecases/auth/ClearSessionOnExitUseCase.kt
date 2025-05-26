package com.example.biketrack.domain.usecases.auth

import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.domain.repositories.AuthRepository

class ClearSessionOnExitUseCase(private val authRepository: AuthRepository) {
    
    suspend operator fun invoke() {
        // Check if there's a stored session
        val hasStoredSession = authRepository.hasStoredSession()
        
        // If no stored session, means last login didn't check "keep session"
        // Therefore, also clear in-memory session
        if (!hasStoredSession) {
            SessionManager.clearSession()
        }
    }
} 