package com.example.biketrack.data.models

import com.example.biketrack.domain.entities.Difficulty
import com.example.biketrack.domain.entities.GeoPoint
import com.example.biketrack.domain.entities.UpdateType
import java.time.LocalDate

data class RouteDto(
    val id: Long,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val imageUrls: List<String>?,
    val city: String,
    val routePoints: List<GeoPoint>,
    val calculatedRoutePoints: List<GeoPoint>?,
    val averageReviewScore: Double,
    val calculatedEstimatedTimeMinutes: Int?,
    val calculatedTotalDistanceKm: Double?,
    val reviews: List<ReviewDto>?,
    val updates: List<RouteUpdateDto>?,
    val reviewCount: Int,
    val updateCount: Int
)

data class RouteSummaryDto(
    val id: Long,
    val title: String,
    val description: String?,
    val difficulty: Difficulty,
    val imageUrls: List<String>?,
    val city: String,
    val routePoints: List<GeoPoint>?,
    val averageReviewScore: Double,
    val reviewCount: Int,
    val updateCount: Int
)

data class RouteCalculationRequest(
    val points: List<GeoPoint>,
    val vehicleType: String
)

data class RouteCalculationResponse(
    val routePoints: List<GeoPoint>,
    val totalDistanceKm: Double,
    val estimatedTimeMinutes: Int,
    val vehicleType: String
) 