package com.example.biketrack.domain.entities

import java.time.LocalDate

data class RouteUpdate(
    val id: Long,
    val description: String,
    val date: LocalDate,
    val type: UpdateType,
    val resolved: Boolean,
    val routeId: Long,
    val userId: Long
)

enum class UpdateType {
    INCIDENT,
    INFO,
    MAINTENANCE,
    CLOSURE,
    OTHER
} 