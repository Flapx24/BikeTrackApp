package com.example.biketrack.domain.entities

data class User(
    val id: Long,
    val username: String,
    val name: String,
    val surname: String?,
    val email: String,
    val imageUrl: String?,
    val role: Role,
    val active: Boolean
)

enum class Role {
    USER,
    ADMIN
} 