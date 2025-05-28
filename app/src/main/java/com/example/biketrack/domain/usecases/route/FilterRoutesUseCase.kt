package com.example.biketrack.domain.usecases.route

import com.example.biketrack.domain.entities.Route
import com.example.biketrack.domain.repositories.RouteRepository
import com.example.biketrack.domain.repositories.RouteResult

class FilterRoutesUseCase(private val routeRepository: RouteRepository) {
    
    suspend operator fun invoke(
        city: String,
        minScore: Int? = null,
        lastRouteId: Long? = null
    ): RouteResult<List<Route>> {
        // Validate that at least one filter criterion is provided
        val hasValidFilters = city.isNotBlank() || (minScore != null && minScore >= 0)
        
        if (!hasValidFilters) {
            return RouteResult.Error("Debe especificar al menos un criterio de filtrado")
        }
        
        return routeRepository.filterRoutes(
            city = city.trim(),
            minScore = minScore,
            lastRouteId = lastRouteId
        )
    }
} 