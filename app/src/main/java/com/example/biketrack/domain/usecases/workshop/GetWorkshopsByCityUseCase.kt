package com.example.biketrack.domain.usecases.workshop

import com.example.biketrack.domain.entities.Workshop
import com.example.biketrack.domain.repositories.WorkshopRepository
import com.example.biketrack.domain.repositories.WorkshopResult

class GetWorkshopsByCityUseCase(private val workshopRepository: WorkshopRepository) {
    
    suspend operator fun invoke(city: String): WorkshopResult<List<Workshop>> {
        if (city.isBlank()) {
            return WorkshopResult.Error("Debe especificar una ciudad")
        }
        
        return workshopRepository.getWorkshopsByCity(city.trim())
    }
} 