package com.example.biketrack.domain.entities

data class BicycleComponent(
    val id: Long,
    val name: String,
    val maxKilometers: Double,
    val currentKilometers: Double
)