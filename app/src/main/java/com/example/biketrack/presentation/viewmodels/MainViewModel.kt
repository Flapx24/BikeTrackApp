package com.example.biketrack.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biketrack.domain.repositories.AuthResult
import com.example.biketrack.domain.usecases.auth.LogoutUseCase
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoggingOut: Boolean = false,
    val logoutSuccess: Boolean = false,
    val errorMessage: String? = null
)

class MainViewModel(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    var uiState by mutableStateOf(MainUiState())
        private set
    
    fun logout() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoggingOut = true, errorMessage = null)
            
            val result = logoutUseCase()
            
            when (result) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoggingOut = false,
                        logoutSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    uiState = uiState.copy(
                        isLoggingOut = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
    
    fun resetLogoutState() {
        uiState = uiState.copy(logoutSuccess = false)
    }
} 