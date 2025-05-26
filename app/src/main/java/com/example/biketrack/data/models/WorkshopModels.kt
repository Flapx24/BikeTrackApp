package com.example.biketrack.data.models

import com.example.biketrack.domain.entities.GeoPoint

data class WorkshopDto(
    val id: Long,
    val name: String,
    val city: String,
    val imageUrls: List<String>,
    val address: String,
    val coordinates: GeoPoint
) 