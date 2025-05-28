package com.example.biketrack.domain.usecases.route

import com.example.biketrack.domain.entities.Route
import com.example.biketrack.domain.repositories.RouteRepository
import com.example.biketrack.domain.repositories.RouteResult

class GetRoutesUseCase(private val routeRepository: RouteRepository) {
    
    suspend operator fun invoke(lastRouteId: Long? = null): RouteResult<List<Route>> {
        return routeRepository.getAllRoutes(lastRouteId)
    }
} 