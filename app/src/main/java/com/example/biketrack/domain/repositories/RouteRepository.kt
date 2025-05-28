package com.example.biketrack.domain.repositories

import com.example.biketrack.domain.entities.Route

sealed class RouteResult<T> {
    data class Success<T>(val data: T) : RouteResult<T>()
    data class Error<T>(val message: String) : RouteResult<T>()
}

interface RouteRepository {
    suspend fun getAllRoutes(lastRouteId: Long? = null): RouteResult<List<Route>>
    suspend fun filterRoutes(
        city: String,
        minScore: Int? = null,
        lastRouteId: Long? = null
    ): RouteResult<List<Route>>
} 