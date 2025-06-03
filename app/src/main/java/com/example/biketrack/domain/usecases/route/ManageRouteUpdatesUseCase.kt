package com.example.biketrack.domain.usecases.route

import com.example.biketrack.domain.entities.RouteUpdate
import com.example.biketrack.domain.entities.UpdateType
import com.example.biketrack.domain.repositories.RouteRepository
import com.example.biketrack.domain.repositories.RouteResult
import java.time.LocalDate

class ManageRouteUpdatesUseCase(private val routeRepository: RouteRepository) {
    
    suspend fun createRouteUpdate(
        routeId: Long,
        description: String,
        date: LocalDate,
        type: UpdateType,
        resolved: Boolean = false
    ): RouteResult<RouteUpdate> {
        if (routeId <= 0) {
            return RouteResult.Error("ID de ruta inválido")
        }
        
        if (description.isBlank()) {
            return RouteResult.Error("La descripción es obligatoria")
        }
        
        return routeRepository.createRouteUpdate(
            routeId = routeId,
            description = description.trim(),
            date = date,
            type = type,
            resolved = resolved
        )
    }
    
    suspend fun updateRouteUpdate(
        id: Long,
        description: String,
        date: LocalDate,
        type: UpdateType,
        resolved: Boolean
    ): RouteResult<RouteUpdate> {
        if (id <= 0) {
            return RouteResult.Error("ID de actualización inválido")
        }
        
        if (description.isBlank()) {
            return RouteResult.Error("La descripción es obligatoria")
        }
        
        return routeRepository.updateRouteUpdate(
            id = id,
            description = description.trim(),
            date = date,
            type = type,
            resolved = resolved
        )
    }
    
    suspend fun deleteRouteUpdate(routeUpdateId: Long): RouteResult<Unit> {
        if (routeUpdateId <= 0) {
            return RouteResult.Error("ID de actualización inválido")
        }
        
        return routeRepository.deleteRouteUpdate(routeUpdateId)
    }
} 