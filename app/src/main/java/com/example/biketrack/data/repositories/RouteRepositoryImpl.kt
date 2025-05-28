package com.example.biketrack.data.repositories

import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.data.mappers.toDomain
import com.example.biketrack.data.remote.RouteApiService
import com.example.biketrack.domain.entities.Route
import com.example.biketrack.domain.repositories.RouteRepository
import com.example.biketrack.domain.repositories.RouteResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RouteRepositoryImpl(
    private val routeApiService: RouteApiService
) : RouteRepository {

    override suspend fun getAllRoutes(lastRouteId: Long?): RouteResult<List<Route>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val response = routeApiService.getAllRoutes(authHeader, lastRouteId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val routes = apiResponse.data?.map { it.toDomain() } ?: emptyList()
                        RouteResult.Success(routes)
                    } else {
                        RouteResult.Error(apiResponse?.message ?: "Error desconocido")
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }

    override suspend fun filterRoutes(
        city: String,
        minScore: Int?,
        lastRouteId: Long?
    ): RouteResult<List<Route>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val response = if (city.isBlank()) {
                    if (minScore != null && minScore >= 0) {
                        routeApiService.filterRoutesByRating(authHeader, minScore, lastRouteId)
                    } else {
                        routeApiService.getAllRoutes(authHeader, lastRouteId)
                    }
                } else {
                    routeApiService.filterRoutes(authHeader, city, minScore, lastRouteId)
                }

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val routes = apiResponse.data?.map { it.toDomain() } ?: emptyList()
                        RouteResult.Success(routes)
                    } else {
                        RouteResult.Error(apiResponse?.message ?: "Error desconocido")
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
} 