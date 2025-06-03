package com.example.biketrack.data.mappers

import com.example.biketrack.data.models.ReviewDto
import com.example.biketrack.data.models.RouteUpdateDto
import com.example.biketrack.domain.entities.Review
import com.example.biketrack.domain.entities.RouteUpdate

fun ReviewDto.toDomain(): Review? {
    return try {
        if (this.date == null) {
            android.util.Log.w("ReviewMapper", "Skipping review ${this.id} due to null date")
            return null
        }
        
        Review(
            id = this.id,
            user = this.user.toDomain(),
            rating = this.rating,
            text = this.text,
            date = this.date,
            routeId = this.routeId
        )
    } catch (e: Exception) {
        android.util.Log.e("ReviewMapper", "Error mapping review ${this.id}: Field values - id: ${this.id}, user: ${this.user}, rating: ${this.rating}, text: ${this.text}, date: ${this.date}, routeId: ${this.routeId}. Error: ${e.message}", e)
        null
    }
}

fun RouteUpdateDto.toDomain(): RouteUpdate? {
    return try {
        if (this.date == null) {
            android.util.Log.w("ReviewMapper", "Skipping route update ${this.id} due to null date")
            return null
        }
        
        RouteUpdate(
            id = this.id,
            description = this.description,
            date = this.date,
            type = this.type,
            resolved = this.resolved,
            routeId = this.routeId,
            userId = this.userId
        )
    } catch (e: Exception) {
        android.util.Log.e("ReviewMapper", "Error mapping route update ${this.id}: ${e.message}", e)
        null
    }
} 