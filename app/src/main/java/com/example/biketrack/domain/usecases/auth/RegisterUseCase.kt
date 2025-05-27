package com.example.biketrack.domain.usecases.auth

import com.example.biketrack.data.models.RegisterRequest
import com.example.biketrack.domain.repositories.AuthRepository
import com.example.biketrack.domain.repositories.AuthResult

class RegisterUseCase(private val authRepository: AuthRepository) {
    
    suspend operator fun invoke(
        username: String,
        name: String,
        surname: String?,
        email: String,
        password: String
    ): AuthResult<String> {
        val request = RegisterRequest(
            username = username.trim(),
            name = name.trim(),
            surname = surname?.trim(),
            email = email.trim(),
            password = password
        )
        
        return authRepository.register(request)
    }
} 