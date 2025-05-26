package com.example.biketrack.core.session

import com.example.biketrack.data.local.UserSession

object SessionManager {
    private var currentUserSession: UserSession? = null
    
    fun setUserSession(userSession: UserSession) {
        currentUserSession = userSession
    }
    
    fun getUserSession(): UserSession? {
        return currentUserSession
    }
    
    fun clearSession() {
        currentUserSession = null
    }
    
    fun isLoggedIn(): Boolean {
        return currentUserSession != null
    }
    
    fun getToken(): String? {
        return currentUserSession?.token
    }
    
    fun getUserId(): Long? {
        return currentUserSession?.id
    }
    
    fun getName(): String? {
        return currentUserSession?.name
    }
    
    fun getImageUrl(): String? {
        return currentUserSession?.imageUrl
    }
    
    fun getAuthHeader(): String? {
        return getToken()?.let { "Bearer $it" }
    }
} 