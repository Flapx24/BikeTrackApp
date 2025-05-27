package com.example.biketrack.data.repositories

import com.example.biketrack.data.local.SecureStorageManager
import com.example.biketrack.data.local.UserSession
import com.example.biketrack.data.models.LoginRequest
import com.example.biketrack.data.models.LoginResponse
import com.example.biketrack.data.models.RegisterRequest
import com.example.biketrack.data.models.RegisterResponse
import com.example.biketrack.data.remote.AuthApiService
import com.example.biketrack.domain.repositories.AuthRepository
import com.example.biketrack.domain.repositories.AuthResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val secureStorageManager: SecureStorageManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): AuthResult<UserSession> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.login(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) {
                        val userSession = UserSession(
                            token = loginResponse.token,
                            id = loginResponse.id,
                            name = loginResponse.name,
                            imageUrl = null
                        )
                        AuthResult.Success(userSession)
                    } else {
                        AuthResult.Error(loginResponse?.message ?: "Error desconocido")
                    }
                } else {
                    // Try to extract error message from API response
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, LoginResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    AuthResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                AuthResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun register(request: RegisterRequest): AuthResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.register(request)

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null && registerResponse.success) {
                        AuthResult.Success(registerResponse.message)
                    } else {
                        AuthResult.Error(registerResponse?.message ?: "Error desconocido")
                    }
                } else {
                    // Try to extract error message from API response
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, RegisterResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    AuthResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                AuthResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun tokenLogin(token: String): AuthResult<UserSession> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $token"
                val response = authApiService.tokenLogin(authHeader)

                if (response.isSuccessful) {
                    val tokenResponse = response.body()
                    if (tokenResponse != null && tokenResponse.success) {
                        val userSession = UserSession(
                            token = token,
                            id = tokenResponse.id ?: 0,
                            name = tokenResponse.name ?: "",
                            imageUrl = null
                        )
                        AuthResult.Success(userSession)
                    } else {
                        AuthResult.Error(tokenResponse?.message ?: "Token inválido")
                    }
                } else {
                    AuthResult.Error("Token inválido o expirado")
                }
            } catch (e: Exception) {
                AuthResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun logout(): AuthResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val cleared = secureStorageManager.clearUserSession()
                if (cleared) {
                    AuthResult.Success(Unit)
                } else {
                    AuthResult.Error("Error al cerrar sesión")
                }
            } catch (e: Exception) {
                AuthResult.Error("Error al cerrar sesión: ${e.message}")
            }
        }
    }
    
    override suspend fun saveUserSession(userSession: UserSession): Boolean {
        return withContext(Dispatchers.IO) {
            secureStorageManager.saveUserSession(userSession)
        }
    }
    
    override suspend fun getUserSession(): UserSession? {
        return withContext(Dispatchers.IO) {
            secureStorageManager.getUserSession()
        }
    }
    
    override suspend fun clearUserSession(): Boolean {
        return withContext(Dispatchers.IO) {
            secureStorageManager.clearUserSession()
        }
    }
    
    override suspend fun hasStoredSession(): Boolean {
        return withContext(Dispatchers.IO) {
            secureStorageManager.hasStoredSession()
        }
    }
} 