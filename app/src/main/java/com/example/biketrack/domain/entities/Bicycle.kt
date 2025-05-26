package com.example.biketrack.domain.entities

import java.time.LocalDate

data class Bicycle(
    val id: Long,
    val name: String,
    val iconUrl: String?,
    val ownerId: Long,
    val totalKilometers: Double,
    val lastMaintenanceDate: LocalDate?,
    val componentCount: Int,
    val components: List<BicycleComponent>
)

data class BicycleSummary(
    val id: Long,
    val name: String,
    val iconUrl: String?,
    val ownerId: Long,
    val totalKilometers: Double,
    val lastMaintenanceDate: LocalDate?,
    val needsMaintenance: Boolean,
    val componentCount: Int
) 