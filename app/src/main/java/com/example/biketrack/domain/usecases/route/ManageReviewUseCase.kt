package com.example.biketrack.domain.usecases.route

import com.example.biketrack.domain.entities.Review
import com.example.biketrack.domain.repositories.RouteRepository
import com.example.biketrack.domain.repositories.RouteResult

class ManageReviewUseCase(private val routeRepository: RouteRepository) {
    
    suspend fun getCurrentUserReview(routeId: Long): RouteResult<Review?> {
        if (routeId <= 0) {
            return RouteResult.Error("ID de ruta inválido")
        }
        
        return routeRepository.getCurrentUserReview(routeId)
    }
    
    suspend fun createReview(routeId: Long, text: String?, rating: Int): RouteResult<Review> {
        if (routeId <= 0) {
            return RouteResult.Error("ID de ruta inválido")
        }
        
        if (rating < 1 || rating > 5) {
            return RouteResult.Error("La puntuación debe estar entre 1 y 5")
        }
        
        return routeRepository.createReview(routeId, text?.trim(), rating)
    }
    
    suspend fun updateReview(routeId: Long, text: String?, rating: Int): RouteResult<Review> {
        if (routeId <= 0) {
            return RouteResult.Error("ID de ruta inválido")
        }
        
        if (rating < 1 || rating > 5) {
            return RouteResult.Error("La puntuación debe estar entre 1 y 5")
        }
        
        return routeRepository.updateReview(routeId, text?.trim(), rating)
    }
    
    suspend fun deleteReview(routeId: Long): RouteResult<Unit> {
        if (routeId <= 0) {
            return RouteResult.Error("ID de ruta inválido")
        }
        
        return routeRepository.deleteReview(routeId)
    }
} 