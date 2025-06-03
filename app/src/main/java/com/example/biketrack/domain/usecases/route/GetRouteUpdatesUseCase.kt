package com.example.biketrack.domain.usecases.route

import com.example.biketrack.domain.entities.RouteUpdate
import com.example.biketrack.domain.repositories.RouteRepository
import com.example.biketrack.domain.repositories.RouteResult

class GetRouteUpdatesUseCase(private val routeRepository: RouteRepository) {
    
    suspend operator fun invoke(routeId: Long): RouteResult<List<RouteUpdate>> {
        if (routeId <= 0) {
            return RouteResult.Error("ID de ruta inválido")
        }
        
        return routeRepository.getRouteUpdates(routeId)
    }
} 