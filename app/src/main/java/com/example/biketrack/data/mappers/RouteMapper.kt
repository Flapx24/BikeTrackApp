package com.example.biketrack.data.mappers

import com.example.biketrack.data.models.RouteDto
import com.example.biketrack.data.models.RouteSummaryDto
import com.example.biketrack.domain.entities.Route

fun RouteDto.toDomain(): Route {
    return try {
        Route(
            id = this.id,
            title = this.title,
            description = this.description,
            difficulty = this.difficulty,
            imageUrls = this.imageUrls ?: emptyList(),
            city = this.city,
            routePoints = this.routePoints,
            calculatedRoutePoints = this.calculatedRoutePoints,
            averageReviewScore = this.averageReviewScore,
            calculatedEstimatedTimeMinutes = this.calculatedEstimatedTimeMinutes,
            calculatedTotalDistanceKm = this.calculatedTotalDistanceKm,
            reviews = try {
                this.reviews?.mapNotNull { it.toDomain() }
            } catch (e: Exception) {
                // If reviews parsing fails completely, use null instead of failing the route
                null
            },
            updates = try {
                this.updates?.mapNotNull { it.toDomain() }
            } catch (e: Exception) {
                // If updates parsing fails completely, use null instead of failing the route
                null
            },
            reviewCount = this.reviewCount,
            updateCount = this.updateCount
        )
    } catch (e: Exception) {
        // If there's still an error, create a route with minimal data
        Route(
            id = this.id,
            title = this.title,
            description = this.description,
            difficulty = this.difficulty,
            imageUrls = this.imageUrls ?: emptyList(),
            city = this.city,
            routePoints = this.routePoints,
            calculatedRoutePoints = this.calculatedRoutePoints,
            averageReviewScore = this.averageReviewScore,
            calculatedEstimatedTimeMinutes = this.calculatedEstimatedTimeMinutes,
            calculatedTotalDistanceKm = this.calculatedTotalDistanceKm,
            reviews = null,
            updates = null,
            reviewCount = this.reviewCount,
            updateCount = this.updateCount
        )
    }
}

fun RouteSummaryDto.toDomain(): Route {
    return Route(
        id = this.id,
        title = this.title,
        description = this.description ?: "",
        difficulty = this.difficulty,
        imageUrls = this.imageUrls ?: emptyList(),
        city = this.city,
        routePoints = this.routePoints ?: emptyList(),
        calculatedRoutePoints = null,
        averageReviewScore = this.averageReviewScore,
        calculatedEstimatedTimeMinutes = null,
        calculatedTotalDistanceKm = null,
        reviews = null,
        updates = null,
        reviewCount = this.reviewCount,
        updateCount = this.updateCount
    )
} 