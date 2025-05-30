package com.example.biketrack.domain.repositories

import com.example.biketrack.domain.entities.Workshop

sealed class WorkshopResult<T> {
    data class Success<T>(val data: T) : WorkshopResult<T>()
    data class Error<T>(val message: String) : WorkshopResult<T>()
}

interface WorkshopRepository {
    suspend fun getWorkshopById(workshopId: Long): WorkshopResult<Workshop>
    suspend fun getWorkshopsByCity(city: String): WorkshopResult<List<Workshop>>
} 