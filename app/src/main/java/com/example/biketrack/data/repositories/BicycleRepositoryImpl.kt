package com.example.biketrack.data.repositories

import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.data.mappers.toDomain
import com.example.biketrack.data.models.*
import com.example.biketrack.data.remote.BicycleApiService
import com.example.biketrack.data.remote.ComponentApiService
import com.example.biketrack.domain.entities.Bicycle
import com.example.biketrack.domain.entities.BicycleSummary
import com.example.biketrack.domain.entities.BicycleComponent
import com.example.biketrack.domain.repositories.BicycleRepository
import com.example.biketrack.domain.repositories.BicycleResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BicycleRepositoryImpl(
    private val bicycleApiService: BicycleApiService,
    private val componentApiService: ComponentApiService
) : BicycleRepository {

    override suspend fun getAllUserBicycles(): BicycleResult<List<BicycleSummary>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val response = bicycleApiService.getAllUserBicycles(authHeader)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val bicycles = apiResponse.data?.map { it.toDomain() } ?: emptyList()
                        BicycleResult.Success(bicycles)
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun getBicycleById(bicycleId: Long): BicycleResult<Bicycle> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val response = bicycleApiService.getBicycleById(authHeader, bicycleId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val bicycle = apiResponse.data?.toDomain()
                        if (bicycle != null) {
                            BicycleResult.Success(bicycle)
                        } else {
                            BicycleResult.Error("Error al procesar datos de la bicicleta")
                        }
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun createBicycle(
        name: String,
        iconUrl: String?,
        totalKilometers: Double,
        lastMaintenanceDate: String?
    ): BicycleResult<Bicycle> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val request = CreateBicycleRequest(
                    name = name,
                    iconUrl = iconUrl,
                    totalKilometers = totalKilometers,
                    lastMaintenanceDate = lastMaintenanceDate
                )
                val response = bicycleApiService.createBicycle(authHeader, request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val bicycle = apiResponse.data?.toDomain()
                        if (bicycle != null) {
                            BicycleResult.Success(bicycle)
                        } else {
                            BicycleResult.Error("Error al procesar la bicicleta creada")
                        }
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun updateBicycle(
        bicycleId: Long,
        name: String,
        iconUrl: String?,
        totalKilometers: Double,
        lastMaintenanceDate: String?
    ): BicycleResult<Bicycle> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val request = UpdateBicycleRequest(
                    name = name,
                    iconUrl = iconUrl,
                    totalKilometers = totalKilometers,
                    lastMaintenanceDate = lastMaintenanceDate,
                    components = emptyList()
                )
                val response = bicycleApiService.updateBicycle(authHeader, bicycleId, request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val bicycle = apiResponse.data?.toDomain()
                        if (bicycle != null) {
                            BicycleResult.Success(bicycle)
                        } else {
                            BicycleResult.Error("Error al procesar la bicicleta actualizada")
                        }
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun deleteBicycle(bicycleId: Long): BicycleResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val response = bicycleApiService.deleteBicycle(authHeader, bicycleId)

                if (response.isSuccessful) {
                    BicycleResult.Success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun addKilometers(bicycleId: Long, kilometers: Double): BicycleResult<Bicycle> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val response = bicycleApiService.addKilometers(authHeader, bicycleId, kilometers)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val bicycle = apiResponse.data?.toDomain()
                        if (bicycle != null) {
                            BicycleResult.Success(bicycle)
                        } else {
                            BicycleResult.Error("Error al procesar la bicicleta actualizada")
                        }
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun subtractKilometers(bicycleId: Long, kilometers: Double): BicycleResult<Bicycle> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val response = bicycleApiService.subtractKilometers(authHeader, bicycleId, kilometers)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val bicycle = apiResponse.data?.toDomain()
                        if (bicycle != null) {
                            BicycleResult.Success(bicycle)
                        } else {
                            BicycleResult.Error("Error al procesar la bicicleta actualizada")
                        }
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    // Component management methods implementation
    override suspend fun createComponent(
        bicycleId: Long,
        name: String,
        maxKilometers: Double,
        currentKilometers: Double
    ): BicycleResult<BicycleComponent> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val request = CreateComponentRequest(
                    name = name,
                    maxKilometers = maxKilometers,
                    currentKilometers = currentKilometers
                )
                val response = componentApiService.createComponent(authHeader, bicycleId, request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val component = apiResponse.data?.toDomain()
                        if (component != null) {
                            BicycleResult.Success(component)
                        } else {
                            BicycleResult.Error("Error al procesar el componente creado")
                        }
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun updateComponent(
        componentId: Long,
        name: String,
        maxKilometers: Double,
        currentKilometers: Double
    ): BicycleResult<BicycleComponent> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val request = UpdateComponentRequest(
                    name = name,
                    maxKilometers = maxKilometers,
                    currentKilometers = currentKilometers
                )
                val response = componentApiService.updateComponent(authHeader, componentId, request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val component = apiResponse.data?.toDomain()
                        if (component != null) {
                            BicycleResult.Success(component)
                        } else {
                            BicycleResult.Error("Error al procesar el componente actualizado")
                        }
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun deleteComponent(componentId: Long): BicycleResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val response = componentApiService.deleteComponent(authHeader, componentId)

                if (response.isSuccessful) {
                    BicycleResult.Success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun getComponentById(componentId: Long): BicycleResult<BicycleComponent> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val response = componentApiService.getComponentById(authHeader, componentId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val component = apiResponse.data?.toDomain()
                        if (component != null) {
                            BicycleResult.Success(component)
                        } else {
                            BicycleResult.Error("Error al procesar datos del componente")
                        }
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun getAllComponentsForBicycle(bicycleId: Long): BicycleResult<List<BicycleComponent>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val response = componentApiService.getAllComponentsForBicycle(authHeader, bicycleId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val components = apiResponse.data?.map { it.toDomain() } ?: emptyList()
                        BicycleResult.Success(components)
                    } else {
                        BicycleResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun resetComponentKilometers(bicycleId: Long): BicycleResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext BicycleResult.Error("No authentication token")

                val response = componentApiService.resetComponentKilometers(authHeader, bicycleId)

                if (response.isSuccessful) {
                    BicycleResult.Success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    BicycleResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                BicycleResult.Error("Error de red: ${e.message}")
            }
        }
    }
} 