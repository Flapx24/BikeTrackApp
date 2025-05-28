package com.example.biketrack.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biketrack.domain.entities.Route
import com.example.biketrack.domain.repositories.RouteResult
import com.example.biketrack.domain.usecases.route.FilterRoutesUseCase
import com.example.biketrack.domain.usecases.route.GetRoutesUseCase
import kotlinx.coroutines.launch

data class RoutesUiState(
    val isLoading: Boolean = false,
    val routes: List<Route> = emptyList(),
    val errorMessage: String? = null,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val searchQuery: String = "",
    val selectedCity: String = "",
    val minRating: Double = 0.0,
    val isFiltering: Boolean = false,
    val showScrollToTopButton: Boolean = false
)

class RoutesViewModel(
    private val getRoutesUseCase: GetRoutesUseCase,
    private val filterRoutesUseCase: FilterRoutesUseCase
) : ViewModel() {
    
    var uiState by mutableStateOf(RoutesUiState())
        private set
    
    init {
        loadRoutes()
    }
    
    fun loadRoutes(isLoadMore: Boolean = false) {
        if (uiState.isLoading || (isLoadMore && !uiState.hasMoreData)) return
        
        viewModelScope.launch {
            if (isLoadMore) {
                uiState = uiState.copy(isLoadingMore = true)
            } else {
                uiState = uiState.copy(isLoading = true, routes = emptyList(), errorMessage = null)
            }
            
            val lastRouteId = if (isLoadMore && uiState.routes.isNotEmpty()) {
                uiState.routes.last().id
            } else {
                null
            }
            
            val result = if (uiState.isFiltering) {
                filterRoutesUseCase(
                    city = uiState.selectedCity,
                    minScore = if (uiState.minRating > 0.0) uiState.minRating.toInt() else null,
                    lastRouteId = lastRouteId
                )
            } else {
                getRoutesUseCase(lastRouteId)
            }
            
            when (result) {
                is RouteResult.Success -> {
                    val newRoutes = result.data
                    uiState = if (isLoadMore) {
                        uiState.copy(
                            isLoadingMore = false,
                            routes = uiState.routes + newRoutes,
                            hasMoreData = newRoutes.isNotEmpty()
                        )
                    } else {
                        uiState.copy(
                            isLoading = false,
                            routes = newRoutes,
                            hasMoreData = newRoutes.isNotEmpty()
                        )
                    }
                }
                is RouteResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    fun filterRoutes() {
        // Validate that at least one filter criterion is met
        val hasValidFilters = uiState.selectedCity.isNotBlank() || uiState.minRating > 0.0
        
        if (!hasValidFilters) {
            // If no valid filters, load all routes
            uiState = uiState.copy(isFiltering = false)
            loadRoutes()
            return
        }
        
        uiState = uiState.copy(isFiltering = true)
        loadRoutes()
    }
    
    fun clearFilters() {
        uiState = uiState.copy(
            selectedCity = "",
            minRating = 0.0,
            isFiltering = false
        )
        loadRoutes()
    }
    
    fun updateSelectedCity(city: String) {
        uiState = uiState.copy(selectedCity = city)
    }
    
    fun updateMinRating(rating: Double) {
        uiState = uiState.copy(minRating = rating)
    }
    
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
    
    fun updateScrollToTopButtonVisibility(show: Boolean) {
        uiState = uiState.copy(showScrollToTopButton = show)
    }
    
    fun retryLoading() {
        loadRoutes()
    }
} 