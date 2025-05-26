package com.example.biketrack.domain.usecases.auth

import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.domain.repositories.AuthRepository
import com.example.biketrack.domain.repositories.AuthResult

class LogoutUseCase(private val authRepository: AuthRepository) {
    
    suspend operator fun invoke(): AuthResult<Unit> {
        // Clear in-memory session
        SessionManager.clearSession()
        
        // Clear secure storage
        return authRepository.logout()
    }
} 