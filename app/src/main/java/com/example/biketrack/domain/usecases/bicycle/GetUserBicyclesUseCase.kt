package com.example.biketrack.domain.usecases.bicycle

import com.example.biketrack.domain.entities.BicycleSummary
import com.example.biketrack.domain.repositories.BicycleRepository
import com.example.biketrack.domain.repositories.BicycleResult

class GetUserBicyclesUseCase(private val bicycleRepository: BicycleRepository) {
    
    suspend operator fun invoke(): BicycleResult<List<BicycleSummary>> {
        return bicycleRepository.getAllUserBicycles()
    }
} 