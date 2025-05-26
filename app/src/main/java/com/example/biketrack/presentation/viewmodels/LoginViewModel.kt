package com.example.biketrack.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biketrack.data.local.UserSession
import com.example.biketrack.domain.repositories.AuthResult
import com.example.biketrack.domain.usecases.auth.AutoLoginUseCase
import com.example.biketrack.domain.usecases.auth.LoginUseCase
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isAutoLogging: Boolean = false,
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val userSession: UserSession? = null
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val autoLoginUseCase: AutoLoginUseCase
) : ViewModel() {
    
    var uiState by mutableStateOf(LoginUiState())
        private set
    
    fun updateEmail(email: String) {
        uiState = uiState.copy(email = email, errorMessage = null)
    }
    
    fun updatePassword(password: String) {
        uiState = uiState.copy(password = password, errorMessage = null)
    }
    
    fun updateRememberMe(rememberMe: Boolean) {
        uiState = uiState.copy(rememberMe = rememberMe)
    }
    
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
    
    fun login() {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Por favor, completa todos los campos")
            return
        }
        
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            
            val result = loginUseCase(
                email = uiState.email.trim(),
                password = uiState.password,
                rememberMe = uiState.rememberMe
            )
            
            when (result) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        userSession = result.data
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    fun attemptAutoLogin() {
        viewModelScope.launch {
            uiState = uiState.copy(isAutoLogging = true)
            
            val result = autoLoginUseCase()
            
            when (result) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isAutoLogging = false,
                        isLoginSuccessful = true,
                        userSession = result.data
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isAutoLogging = false,
                        errorMessage = if (result.message.contains("No hay sesión guardada") || 
                                         result.message.contains("No stored session") ||
                                         result.message.contains("user must login manually")) {
                            null
                        } else {
                            result.message
                        }
                    )
                }
            }
        }
    }
    
    fun resetLoginState() {
        uiState = uiState.copy(isLoginSuccessful = false, userSession = null)
    }
} 