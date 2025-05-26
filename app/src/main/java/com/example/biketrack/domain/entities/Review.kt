package com.example.biketrack.domain.entities

import java.time.LocalDate

data class Review(
    val id: Long,
    val user: UserSummary,
    val rating: Int,
    val text: String?,
    val date: LocalDate,
    val routeId: Long
)

data class UserSummary(
    val id: Long,
    val username: String,
    val imageUrl: String?
) 