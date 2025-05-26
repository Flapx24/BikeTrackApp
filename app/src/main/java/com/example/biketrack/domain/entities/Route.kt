package com.example.biketrack.domain.entities

data class Route(
    val id: Long,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val imageUrls: List<String>,
    val city: String,
    val routePoints: List<GeoPoint>,
    val calculatedRoutePoints: List<GeoPoint>?,
    val averageReviewScore: Double,
    val calculatedEstimatedTimeMinutes: Int?,
    val calculatedTotalDistanceKm: Double?,
    val reviews: List<Review>?,
    val updates: List<RouteUpdate>?,
    val reviewCount: Int,
    val updateCount: Int
)

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
} 