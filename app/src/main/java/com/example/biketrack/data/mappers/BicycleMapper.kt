package com.example.biketrack.data.mappers

import com.example.biketrack.data.models.BicycleDto
import com.example.biketrack.data.models.BicycleSummaryDto
import com.example.biketrack.data.models.BicycleComponentDto
import com.example.biketrack.domain.entities.Bicycle
import com.example.biketrack.domain.entities.BicycleSummary
import com.example.biketrack.domain.entities.BicycleComponent

fun BicycleDto.toDomain(): Bicycle {
    return Bicycle(
        id = this.id,
        name = this.name,
        iconUrl = this.iconUrl,
        ownerId = this.ownerId,
        totalKilometers = this.totalKilometers,
        lastMaintenanceDate = this.lastMaintenanceDate,
        componentCount = this.componentCount,
        components = this.components.map { it.toDomain() }
    )
}

fun BicycleSummaryDto.toDomain(): BicycleSummary {
    return BicycleSummary(
        id = this.id,
        name = this.name,
        iconUrl = this.iconUrl,
        ownerId = this.ownerId,
        totalKilometers = this.totalKilometers,
        lastMaintenanceDate = this.lastMaintenanceDate,
        needsMaintenance = this.needsMaintenance == "true",
        componentCount = this.componentCount
    )
}

fun BicycleComponentDto.toDomain(): BicycleComponent {
    return BicycleComponent(
        id = this.id,
        name = this.name,
        maxKilometers = this.maxKilometers,
        currentKilometers = this.currentKilometers
    )
} 