package com.example.biketrack.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biketrack.domain.entities.Workshop
import com.example.biketrack.domain.repositories.WorkshopResult
import com.example.biketrack.domain.usecases.workshop.GetWorkshopByIdUseCase
import com.example.biketrack.domain.usecases.workshop.GetWorkshopsByCityUseCase
import kotlinx.coroutines.launch

data class WorkshopsUiState(
    val isLoading: Boolean = false,
    val workshops: List<Workshop> = emptyList(),
    val searchCity: String = "",
    val lastSearchedCity: String = "",
    val errorMessage: String? = null,
    val hasSearched: Boolean = false,
    val showScrollToTopButton: Boolean = false,
    val showMap: Boolean = false,
    val selectedWorkshop: Workshop? = null,
    val showWorkshopModal: Boolean = false,
    val currentImageIndex: Int = 0
)

class WorkshopsViewModel(
    private val getWorkshopsByCityUseCase: GetWorkshopsByCityUseCase,
    private val getWorkshopByIdUseCase: GetWorkshopByIdUseCase
) : ViewModel() {
    
    var uiState by mutableStateOf(WorkshopsUiState())
        private set
    
    fun updateSearchCity(city: String) {
        uiState = uiState.copy(searchCity = city, errorMessage = null)
    }
    
    fun searchWorkshops() {
        if (uiState.searchCity.isBlank()) {
            uiState = uiState.copy(errorMessage = "Debe especificar una ciudad para buscar")
            return
        }
        
        viewModelScope.launch {
            val cityToSearch = uiState.searchCity
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            
            val result = getWorkshopsByCityUseCase(cityToSearch)
            
            when (result) {
                is WorkshopResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        workshops = result.data,
                        lastSearchedCity = cityToSearch,
                        hasSearched = true
                    )
                }
                is WorkshopResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        workshops = emptyList(),
                        lastSearchedCity = cityToSearch,
                        hasSearched = true
                    )
                }
            }
        }
    }
    
    fun clearSearchField() {
        uiState = uiState.copy(searchCity = "", errorMessage = null)
    }
    
    fun clearSearch() {
        uiState = uiState.copy(
            searchCity = "",
            lastSearchedCity = "",
            workshops = emptyList(),
            hasSearched = false,
            showMap = false,
            selectedWorkshop = null,
            showWorkshopModal = false,
            errorMessage = null
        )
    }
    
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
    
    fun updateScrollToTopButtonVisibility(show: Boolean) {
        uiState = uiState.copy(showScrollToTopButton = show)
    }
    
    fun showMap() {
        uiState = uiState.copy(showMap = true)
    }
    
    fun hideMap() {
        uiState = uiState.copy(showMap = false, selectedWorkshop = null, showWorkshopModal = false)
    }
    
    fun selectWorkshop(workshop: Workshop) {
        uiState = uiState.copy(
            selectedWorkshop = workshop,
            showWorkshopModal = true,
            currentImageIndex = 0
        )
    }
    
    fun hideWorkshopModal() {
        uiState = uiState.copy(
            showWorkshopModal = false,
            selectedWorkshop = null,
            currentImageIndex = 0
        )
    }
    
    fun onWorkshopCardClick(workshop: Workshop) {
        // Navigate to map view, center on workshop and show modal
        uiState = uiState.copy(
            showMap = true,
            selectedWorkshop = workshop,
            showWorkshopModal = true,
            currentImageIndex = 0
        )
    }
    
    fun updateCurrentImageIndex(index: Int) {
        uiState = uiState.copy(currentImageIndex = index)
    }
    
    fun previousImage() {
        val workshop = uiState.selectedWorkshop ?: return
        if (workshop.imageUrls.isNotEmpty()) {
            val newIndex = if (uiState.currentImageIndex > 0) {
                uiState.currentImageIndex - 1
            } else {
                workshop.imageUrls.size - 1
            }
            uiState = uiState.copy(currentImageIndex = newIndex)
        }
    }
    
    fun nextImage() {
        val workshop = uiState.selectedWorkshop ?: return
        if (workshop.imageUrls.isNotEmpty()) {
            val newIndex = if (uiState.currentImageIndex < workshop.imageUrls.size - 1) {
                uiState.currentImageIndex + 1
            } else {
                0
            }
            uiState = uiState.copy(currentImageIndex = newIndex)
        }
    }
} 