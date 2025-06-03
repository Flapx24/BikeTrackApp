package com.example.biketrack.domain.usecases.route

import com.example.biketrack.domain.entities.Review
import com.example.biketrack.domain.repositories.RouteRepository
import com.example.biketrack.domain.repositories.RouteResult

class GetRouteReviewsUseCase(private val routeRepository: RouteRepository) {
    
    suspend operator fun invoke(routeId: Long, lastReviewId: Long? = null): RouteResult<List<Review>> {
        if (routeId <= 0) {
            return RouteResult.Error("ID de ruta inválido")
        }
        
        return routeRepository.getRouteReviews(routeId, lastReviewId)
    }
} 