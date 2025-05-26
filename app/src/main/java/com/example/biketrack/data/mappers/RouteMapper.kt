package com.example.biketrack.data.mappers

import com.example.biketrack.data.models.RouteDto
import com.example.biketrack.data.models.RouteSummaryDto
import com.example.biketrack.domain.entities.Route

fun RouteDto.toDomain(): Route {
    return Route(
        id = this.id,
        title = this.title,
        description = this.description,
        difficulty = this.difficulty,
        imageUrls = this.imageUrls,
        city = this.city,
        routePoints = this.routePoints,
        calculatedRoutePoints = this.calculatedRoutePoints,
        averageReviewScore = this.averageReviewScore,
        calculatedEstimatedTimeMinutes = this.calculatedEstimatedTimeMinutes,
        calculatedTotalDistanceKm = this.calculatedTotalDistanceKm,
        reviews = this.reviews?.map { it.toDomain() },
        updates = this.updates?.map { it.toDomain() },
        reviewCount = this.reviewCount,
        updateCount = this.updateCount
    )
}

fun RouteSummaryDto.toDomain(): Route {
    return Route(
        id = this.id,
        title = this.title,
        description = this.description,
        difficulty = this.difficulty,
        imageUrls = this.imageUrls,
        city = this.city,
        routePoints = this.routePoints,
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