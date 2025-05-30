package com.example.biketrack.data.repositories

import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.data.mappers.toDomain
import com.example.biketrack.data.remote.WorkshopApiService
import com.example.biketrack.domain.entities.Workshop
import com.example.biketrack.domain.repositories.WorkshopRepository
import com.example.biketrack.domain.repositories.WorkshopResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WorkshopRepositoryImpl(
    private val workshopApiService: WorkshopApiService
) : WorkshopRepository {

    override suspend fun getWorkshopById(workshopId: Long): WorkshopResult<Workshop> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext WorkshopResult.Error("No authentication token")

                val response = workshopApiService.getWorkshopById(authHeader, workshopId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val workshop = apiResponse.data?.toDomain()
                        if (workshop != null) {
                            WorkshopResult.Success(workshop)
                        } else {
                            WorkshopResult.Error("Error al procesar datos del taller")
                        }
                    } else {
                        WorkshopResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, com.example.biketrack.data.models.ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    WorkshopResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                WorkshopResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun getWorkshopsByCity(city: String): WorkshopResult<List<Workshop>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext WorkshopResult.Error("No authentication token")

                val response = workshopApiService.getWorkshopsByCity(authHeader, city.trim())

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val workshops = apiResponse.data?.map { it.toDomain() } ?: emptyList()
                        WorkshopResult.Success(workshops)
                    } else {
                        WorkshopResult.Error(apiResponse?.message ?: "Error desconocido")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        if (errorBody != null) {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, com.example.biketrack.data.models.ApiResponse::class.java)
                            errorResponse.message
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    WorkshopResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                WorkshopResult.Error("Error de red: ${e.message}")
            }
        }
    }
} 