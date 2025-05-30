package com.example.biketrack.domain.usecases.workshop

import com.example.biketrack.domain.entities.Workshop
import com.example.biketrack.domain.repositories.WorkshopRepository
import com.example.biketrack.domain.repositories.WorkshopResult

class GetWorkshopByIdUseCase(private val workshopRepository: WorkshopRepository) {
    
    suspend operator fun invoke(workshopId: Long): WorkshopResult<Workshop> {
        if (workshopId <= 0) {
            return WorkshopResult.Error("ID de taller inválido")
        }
        
        return workshopRepository.getWorkshopById(workshopId)
    }
} 