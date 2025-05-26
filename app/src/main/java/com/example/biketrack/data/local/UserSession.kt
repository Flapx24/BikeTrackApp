package com.example.biketrack.data.local

data class UserSession(
    val token: String,
    val id: Long,
    val username: String,
    val name: String,
    val imageUrl: String?
) 