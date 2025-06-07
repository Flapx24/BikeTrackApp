package com.example.biketrack.domain.repositories

import com.example.biketrack.domain.entities.Bicycle
import com.example.biketrack.domain.entities.BicycleSummary
import com.example.biketrack.domain.entities.BicycleComponent

sealed class BicycleResult<T> {
    data class Success<T>(val data: T) : BicycleResult<T>()
    data class Error<T>(val message: String) : BicycleResult<T>()
}

interface BicycleRepository {
    suspend fun getAllUserBicycles(): BicycleResult<List<BicycleSummary>>
    suspend fun getBicycleById(bicycleId: Long): BicycleResult<Bicycle>
    suspend fun createBicycle(
        name: String,
        iconUrl: String?,
        totalKilometers: Double,
        lastMaintenanceDate: String?
    ): BicycleResult<Bicycle>
    suspend fun updateBicycle(
        bicycleId: Long,
        name: String,
        iconUrl: String?,
        totalKilometers: Double,
        lastMaintenanceDate: String?
    ): BicycleResult<Bicycle>
    suspend fun deleteBicycle(bicycleId: Long): BicycleResult<Unit>
    suspend fun addKilometers(bicycleId: Long, kilometers: Double): BicycleResult<Bicycle>
    suspend fun subtractKilometers(bicycleId: Long, kilometers: Double): BicycleResult<Bicycle>
    
    // Component management methods
    suspend fun createComponent(
        bicycleId: Long,
        name: String,
        maxKilometers: Double,
        currentKilometers: Double
    ): BicycleResult<BicycleComponent>
    suspend fun updateComponent(
        componentId: Long,
        name: String,
        maxKilometers: Double,
        currentKilometers: Double
    ): BicycleResult<BicycleComponent>
    suspend fun deleteComponent(componentId: Long): BicycleResult<Unit>
    suspend fun getComponentById(componentId: Long): BicycleResult<BicycleComponent>
    suspend fun getAllComponentsForBicycle(bicycleId: Long): BicycleResult<List<BicycleComponent>>
    suspend fun resetComponentKilometers(bicycleId: Long): BicycleResult<Unit>
} 