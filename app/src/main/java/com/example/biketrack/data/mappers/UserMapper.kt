package com.example.biketrack.data.mappers

import com.example.biketrack.data.models.UserSummaryDto
import com.example.biketrack.domain.entities.UserSummary

fun UserSummaryDto.toDomain(): UserSummary {
    return UserSummary(
        id = this.id,
        username = this.username,
        imageUrl = this.imageUrl
    )
}

fun UserSummary.toDto(): UserSummaryDto {
    return UserSummaryDto(
        id = this.id,
        username = this.username,
        imageUrl = this.imageUrl
    )
} 