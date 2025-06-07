package com.example.biketrack.domain.usecases.bicycle

import com.example.biketrack.domain.entities.Bicycle
import com.example.biketrack.domain.repositories.BicycleRepository
import com.example.biketrack.domain.repositories.BicycleResult

class GetBicycleByIdUseCase(private val bicycleRepository: BicycleRepository) {
    
    suspend operator fun invoke(bicycleId: Long): BicycleResult<Bicycle> {
        if (bicycleId <= 0) {
            return BicycleResult.Error("ID de bicicleta inválido")
        }
        
        return bicycleRepository.getBicycleById(bicycleId)
    }
} 