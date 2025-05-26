package com.example.biketrack.data.models

import com.example.biketrack.domain.entities.UpdateType
import java.time.LocalDate

data class ReviewDto(
    val id: Long,
    val user: UserSummaryDto,
    val rating: Int,
    val text: String?,
    val date: LocalDate,
    val routeId: Long
)

data class UserSummaryDto(
    val id: Long,
    val username: String,
    val imageUrl: String?
)

data class CreateReviewRequest(
    val text: String?,
    val rating: Int
)

data class UpdateReviewRequest(
    val text: String?,
    val rating: Int
)

data class RouteUpdateDto(
    val id: Long,
    val description: String,
    val date: LocalDate,
    val type: UpdateType,
    val resolved: Boolean,
    val routeId: Long,
    val userId: Long
)

data class CreateRouteUpdateRequest(
    val routeId: Long,
    val description: String,
    val date: String,
    val type: UpdateType,
    val resolved: Boolean = false
)

data class UpdateRouteUpdateRequest(
    val id: Long,
    val description: String,
    val date: String,
    val type: UpdateType,
    val resolved: Boolean
) 