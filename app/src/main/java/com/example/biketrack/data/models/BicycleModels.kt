package com.example.biketrack.data.models

import java.time.LocalDate

data class BicycleDto(
    val id: Long,
    val name: String,
    val iconUrl: String?,
    val ownerId: Long,
    val totalKilometers: Double,
    val lastMaintenanceDate: LocalDate?,
    val componentCount: Int,
    val components: List<BicycleComponentDto>
)

data class BicycleSummaryDto(
    val id: Long,
    val name: String,
    val iconUrl: String?,
    val ownerId: Long,
    val totalKilometers: Double,
    val lastMaintenanceDate: LocalDate?,
    val needsMaintenance: String,
    val componentCount: Int
)

data class BicycleComponentDto(
    val id: Long,
    val name: String,
    val maxKilometers: Double,
    val currentKilometers: Double
)

data class CreateBicycleRequest(
    val name: String,
    val iconUrl: String?,
    val totalKilometers: Double = 0.0,
    val lastMaintenanceDate: String?,
    val components: List<CreateComponentRequest> = emptyList()
)

data class UpdateBicycleRequest(
    val name: String,
    val iconUrl: String?,
    val totalKilometers: Double,
    val lastMaintenanceDate: String?,
    val components: List<CreateComponentRequest>
)

data class CreateComponentRequest(
    val name: String,
    val maxKilometers: Double,
    val currentKilometers: Double
)

data class UpdateComponentRequest(
    val name: String,
    val maxKilometers: Double,
    val currentKilometers: Double
) 