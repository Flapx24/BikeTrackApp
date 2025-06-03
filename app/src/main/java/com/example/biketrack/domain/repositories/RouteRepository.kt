package com.example.biketrack.domain.repositories

import com.example.biketrack.domain.entities.Route
import com.example.biketrack.domain.entities.Review
import com.example.biketrack.domain.entities.RouteUpdate
import com.example.biketrack.domain.entities.UpdateType
import java.time.LocalDate

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
    
    suspend fun getRouteById(routeId: Long): RouteResult<Route>
    
    suspend fun getRouteReviews(routeId: Long, lastReviewId: Long? = null): RouteResult<List<Review>>
    suspend fun getCurrentUserReview(routeId: Long): RouteResult<Review?>
    suspend fun createReview(routeId: Long, text: String?, rating: Int): RouteResult<Review>
    suspend fun updateReview(routeId: Long, text: String?, rating: Int): RouteResult<Review>
    suspend fun deleteReview(routeId: Long): RouteResult<Unit>
    
    suspend fun getRouteUpdates(routeId: Long): RouteResult<List<RouteUpdate>>
    suspend fun createRouteUpdate(
        routeId: Long,
        description: String,
        date: LocalDate,
        type: UpdateType,
        resolved: Boolean = false
    ): RouteResult<RouteUpdate>
    suspend fun updateRouteUpdate(
        id: Long,
        description: String,
        date: LocalDate,
        type: UpdateType,
        resolved: Boolean
    ): RouteResult<RouteUpdate>
    suspend fun deleteRouteUpdate(routeUpdateId: Long): RouteResult<Unit>
} 