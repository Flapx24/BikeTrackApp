package com.example.biketrack.data.mappers

import com.example.biketrack.data.models.WorkshopDto
import com.example.biketrack.domain.entities.Workshop

fun WorkshopDto.toDomain(): Workshop {
    return Workshop(
        id = this.id,
        name = this.name,
        city = this.city,
        imageUrls = this.imageUrls,
        address = this.address,
        coordinates = this.coordinates
    )
} 