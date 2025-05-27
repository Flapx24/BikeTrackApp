package com.example.biketrack.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biketrack.domain.repositories.AuthResult
import com.example.biketrack.domain.usecases.auth.RegisterUseCase
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val username: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    
    var uiState by mutableStateOf(RegisterUiState())
        private set
    
    fun updateUsername(username: String) {
        uiState = uiState.copy(username = username, errorMessage = null)
    }
    
    fun updateName(name: String) {
        uiState = uiState.copy(name = name, errorMessage = null)
    }
    
    fun updateSurname(surname: String) {
        uiState = uiState.copy(surname = surname, errorMessage = null)
    }
    
    fun updateEmail(email: String) {
        uiState = uiState.copy(email = email, errorMessage = null)
    }
    
    fun updatePassword(password: String) {
        uiState = uiState.copy(password = password, errorMessage = null)
    }
    
    fun updateConfirmPassword(confirmPassword: String) {
        uiState = uiState.copy(confirmPassword = confirmPassword, errorMessage = null)
    }
    
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
    
    fun clearSuccess() {
        uiState = uiState.copy(successMessage = null)
    }
    
    private fun validateFields(): String? {
        return when {
            uiState.username.isBlank() -> "El nombre de usuario es obligatorio"
            uiState.name.isBlank() -> "El nombre es obligatorio"
            uiState.email.isBlank() -> "El email es obligatorio"
            !isValidEmail(uiState.email) -> "Por favor, introduce un email válido"
            uiState.password.isBlank() -> "La contraseña es obligatoria"
            uiState.password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            uiState.password != uiState.confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun register() {
        val validationError = validateFields()
        if (validationError != null) {
            uiState = uiState.copy(errorMessage = validationError)
            return
        }
        
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            
            val result = registerUseCase(
                username = uiState.username,
                name = uiState.name,
                surname = uiState.surname.takeIf { it.isNotBlank() },
                email = uiState.email,
                password = uiState.password
            )
            
            when (result) {
                is AuthResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        successMessage = "${result.data}. Redirigiendo al login...",
                        isRegistrationSuccessful = true
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
    
    fun resetRegistrationState() {
        uiState = uiState.copy(isRegistrationSuccessful = false, successMessage = null)
    }
} 