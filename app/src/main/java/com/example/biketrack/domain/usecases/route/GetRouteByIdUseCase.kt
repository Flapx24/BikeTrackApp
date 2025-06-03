package com.example.biketrack.domain.usecases.route

import com.example.biketrack.domain.entities.Route
import com.example.biketrack.domain.repositories.RouteRepository
import com.example.biketrack.domain.repositories.RouteResult

class GetRouteByIdUseCase(private val routeRepository: RouteRepository) {
    
    suspend operator fun invoke(routeId: Long): RouteResult<Route> {
        if (routeId <= 0) {
            return RouteResult.Error("ID de ruta inválido")
        }
        
        return routeRepository.getRouteById(routeId)
    }
} 