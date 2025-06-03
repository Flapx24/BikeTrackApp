package com.example.biketrack.data.repositories

import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.data.mappers.toDomain
import com.example.biketrack.data.models.*
import com.example.biketrack.data.remote.RouteApiService
import com.example.biketrack.domain.entities.Route
import com.example.biketrack.domain.entities.Review
import com.example.biketrack.domain.entities.RouteUpdate
import com.example.biketrack.domain.entities.UpdateType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    
    override suspend fun getRouteById(routeId: Long): RouteResult<Route> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No hay token de autenticación. Por favor, inicia sesión nuevamente.")
                
                val response = routeApiService.getRouteById(authHeader, routeId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val routeDto = apiResponse.data
                        if (routeDto != null) {
                            try {
                                val route = routeDto.toDomain()
                                RouteResult.Success(route)
                            } catch (e: Exception) {
                                // Try to create a fallback route with basic data if possible
                                try {
                                    val fallbackRoute = Route(
                                        id = routeDto.id,
                                        title = routeDto.title,
                                        description = routeDto.description,
                                        difficulty = routeDto.difficulty,
                                        imageUrls = routeDto.imageUrls ?: emptyList(),
                                        city = routeDto.city,
                                        routePoints = routeDto.routePoints,
                                        calculatedRoutePoints = routeDto.calculatedRoutePoints,
                                        averageReviewScore = routeDto.averageReviewScore,
                                        calculatedEstimatedTimeMinutes = routeDto.calculatedEstimatedTimeMinutes,
                                        calculatedTotalDistanceKm = routeDto.calculatedTotalDistanceKm,
                                        reviews = null,
                                        updates = null,
                                        reviewCount = routeDto.reviewCount,
                                        updateCount = routeDto.updateCount
                                    )
                                    RouteResult.Success(fallbackRoute)
                                } catch (fallbackException: Exception) {
                                    RouteResult.Error("Error al procesar datos de la ruta: ${e.message}")
                                }
                            }
                        } else {
                            RouteResult.Error("No se recibieron datos de la ruta")
                        }
                    } else {
                        RouteResult.Error(apiResponse?.message ?: "Error desconocido")
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun getRouteReviews(routeId: Long, lastReviewId: Long?): RouteResult<List<Review>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val response = routeApiService.getRouteReviews(authHeader, routeId, lastReviewId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val reviews = try {
                            apiResponse.data?.mapNotNull { it.toDomain() } ?: emptyList()
                        } catch (e: Exception) {
                            // If there's a parsing error, return empty list
                            emptyList()
                        }
                        RouteResult.Success(reviews)
                    } else {
                        RouteResult.Error(apiResponse?.message ?: "Error desconocido")
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun getCurrentUserReview(routeId: Long): RouteResult<Review?> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val response = routeApiService.getCurrentUserReview(authHeader, routeId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val review = apiResponse.data?.toDomain()
                        RouteResult.Success(review)
                    } else {
                        RouteResult.Success(null) // User has no review
                    }
                } else if (response.code() == 404) {
                    RouteResult.Success(null) // User has no review
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun createReview(routeId: Long, text: String?, rating: Int): RouteResult<Review> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val request = CreateReviewRequest(text = text, rating = rating)
                val response = routeApiService.createReview(authHeader, routeId, request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val review = apiResponse.data?.toDomain()
                        if (review != null) {
                            RouteResult.Success(review)
                        } else {
                            RouteResult.Error("Error al procesar la reseña creada")
                        }
                    } else {
                        RouteResult.Error(apiResponse?.message ?: "Error desconocido")
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun updateReview(routeId: Long, text: String?, rating: Int): RouteResult<Review> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val request = UpdateReviewRequest(text = text, rating = rating)
                val response = routeApiService.updateReview(authHeader, routeId, request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val review = apiResponse.data?.toDomain()
                        if (review != null) {
                            RouteResult.Success(review)
                        } else {
                            RouteResult.Error("Error al procesar la reseña actualizada")
                        }
                    } else {
                        RouteResult.Error(apiResponse?.message ?: "Error desconocido")
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun deleteReview(routeId: Long): RouteResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val response = routeApiService.deleteReview(authHeader, routeId)

                if (response.isSuccessful) {
                    RouteResult.Success(Unit)
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun getRouteUpdates(routeId: Long): RouteResult<List<RouteUpdate>> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val response = routeApiService.getRouteUpdates(authHeader, routeId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val updates = apiResponse.data?.mapNotNull { it.toDomain() } ?: emptyList()
                        RouteResult.Success(updates)
                    } else {
                        RouteResult.Error(apiResponse?.message ?: "Error desconocido")
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun createRouteUpdate(
        routeId: Long,
        description: String,
        date: LocalDate,
        type: UpdateType,
        resolved: Boolean
    ): RouteResult<RouteUpdate> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val request = CreateRouteUpdateRequest(
                    routeId = routeId,
                    description = description,
                    date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    type = type,
                    resolved = resolved
                )
                val response = routeApiService.createRouteUpdate(authHeader, request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val update = apiResponse.data?.toDomain()
                        if (update != null) {
                            RouteResult.Success(update)
                        } else {
                            RouteResult.Error("Error al procesar la actualización creada")
                        }
                    } else {
                        RouteResult.Error(apiResponse?.message ?: "Error desconocido")
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun updateRouteUpdate(
        id: Long,
        description: String,
        date: LocalDate,
        type: UpdateType,
        resolved: Boolean
    ): RouteResult<RouteUpdate> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val request = UpdateRouteUpdateRequest(
                    id = id,
                    description = description,
                    date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    type = type,
                    resolved = resolved
                )
                val response = routeApiService.updateRouteUpdate(authHeader, request)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val update = apiResponse.data?.toDomain()
                        if (update != null) {
                            RouteResult.Success(update)
                        } else {
                            RouteResult.Error("Error al procesar la actualización modificada")
                        }
                    } else {
                        RouteResult.Error(apiResponse?.message ?: "Error desconocido")
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
    
    override suspend fun deleteRouteUpdate(routeUpdateId: Long): RouteResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = SessionManager.getAuthHeader()
                    ?: return@withContext RouteResult.Error("No authentication token")

                val response = routeApiService.deleteRouteUpdate(authHeader, routeUpdateId)

                if (response.isSuccessful) {
                    RouteResult.Success(Unit)
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

                    RouteResult.Error(errorMessage ?: "Error de conexión: ${response.code()}")
                }
            } catch (e: Exception) {
                RouteResult.Error("Error de red: ${e.message}")
            }
        }
    }
} 