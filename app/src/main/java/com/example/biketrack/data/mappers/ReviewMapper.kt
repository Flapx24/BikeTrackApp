package com.example.biketrack.data.mappers

import com.example.biketrack.data.models.ReviewDto
import com.example.biketrack.data.models.RouteUpdateDto
import com.example.biketrack.domain.entities.Review
import com.example.biketrack.domain.entities.RouteUpdate

fun ReviewDto.toDomain(): Review {
    return Review(
        id = this.id,
        user = this.user.toDomain(),
        rating = this.rating,
        text = this.text,
        date = this.date,
        routeId = this.routeId
    )
}

fun RouteUpdateDto.toDomain(): RouteUpdate {
    return RouteUpdate(
        id = this.id,
        description = this.description,
        date = this.date,
        type = this.type,
        resolved = this.resolved,
        routeId = this.routeId,
        userId = this.userId
    )
} 