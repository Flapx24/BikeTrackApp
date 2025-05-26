package com.example.biketrack.domain.entities

data class Workshop(
    val id: Long,
    val name: String,
    val city: String,
    val imageUrls: List<String>,
    val address: String,
    val coordinates: GeoPoint
) 