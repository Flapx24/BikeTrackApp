package com.example.biketrack.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biketrack.domain.entities.Bicycle
import com.example.biketrack.domain.entities.BicycleComponent
import com.example.biketrack.domain.repositories.BicycleRepository
import com.example.biketrack.domain.repositories.BicycleResult
import kotlinx.coroutines.launch

data class BicycleDetailUiState(
    val isLoading: Boolean = false,
    val bicycle: Bicycle? = null,
    val errorMessage: String? = null,
    
    // Component dialogs
    val showCreateComponentDialog: Boolean = false,
    val showEditComponentDialog: Boolean = false,
    val showDeleteComponentDialog: Boolean = false,
    val editingComponent: BicycleComponent? = null,
    val deletingComponent: BicycleComponent? = null,
    
    // Kilometers dialogs
    val showAddKilometersDialog: Boolean = false,
    val showSubtractKilometersDialog: Boolean = false,
    
    // Processing states
    val isCreatingComponent: Boolean = false,
    val isUpdatingComponent: Boolean = false,
    val isDeletingComponent: Boolean = false,
    val isAddingKilometers: Boolean = false,
    val isSubtractingKilometers: Boolean = false,
    
    // Success messages
    val showSuccessMessage: Boolean = false,
    val successMessage: String = ""
)

class BicycleDetailViewModel(
    private val bicycleRepository: BicycleRepository
) : ViewModel() {

    var uiState by mutableStateOf(BicycleDetailUiState())
        private set

    fun loadBicycle(bicycleId: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            
            when (val result = bicycleRepository.getBicycleById(bicycleId)) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        bicycle = result.data,
                        errorMessage = null
                    )
                }
                is BicycleResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun clearSuccessMessage() {
        uiState = uiState.copy(showSuccessMessage = false, successMessage = "")
    }

    // Component dialog management
    fun showCreateComponentDialog() {
        uiState = uiState.copy(showCreateComponentDialog = true)
    }

    fun hideCreateComponentDialog() {
        uiState = uiState.copy(showCreateComponentDialog = false)
    }

    fun showEditComponentDialog(component: BicycleComponent) {
        uiState = uiState.copy(
            showEditComponentDialog = true,
            editingComponent = component
        )
    }

    fun hideEditComponentDialog() {
        uiState = uiState.copy(
            showEditComponentDialog = false,
            editingComponent = null
        )
    }

    fun showDeleteComponentDialog(component: BicycleComponent) {
        uiState = uiState.copy(
            showDeleteComponentDialog = true,
            deletingComponent = component
        )
    }

    fun hideDeleteComponentDialog() {
        uiState = uiState.copy(
            showDeleteComponentDialog = false,
            deletingComponent = null
        )
    }

    // Component operations
    fun createComponent(
        name: String,
        maxKilometers: Double,
        currentKilometers: Double
    ) {
        val bicycleId = uiState.bicycle?.id ?: return
        
        viewModelScope.launch {
            uiState = uiState.copy(isCreatingComponent = true)
            
            when (val result = bicycleRepository.createComponent(bicycleId, name, maxKilometers, currentKilometers)) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isCreatingComponent = false,
                        showCreateComponentDialog = false,
                        showSuccessMessage = true,
                        successMessage = "Componente creado con éxito"
                    )
                    // Reload bicycle to get updated components
                    loadBicycle(bicycleId)
                }
                is BicycleResult.Error -> {
                    uiState = uiState.copy(
                        isCreatingComponent = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun updateComponent(
        componentId: Long,
        name: String,
        maxKilometers: Double,
        currentKilometers: Double
    ) {
        val bicycleId = uiState.bicycle?.id ?: return
        
        viewModelScope.launch {
            uiState = uiState.copy(isUpdatingComponent = true)
            
            when (val result = bicycleRepository.updateComponent(componentId, name, maxKilometers, currentKilometers)) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isUpdatingComponent = false,
                        showEditComponentDialog = false,
                        editingComponent = null,
                        showSuccessMessage = true,
                        successMessage = "Componente actualizado con éxito"
                    )
                    // Reload bicycle to get updated components
                    loadBicycle(bicycleId)
                }
                is BicycleResult.Error -> {
                    uiState = uiState.copy(
                        isUpdatingComponent = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun deleteComponent(componentId: Long) {
        val bicycleId = uiState.bicycle?.id ?: return
        
        viewModelScope.launch {
            uiState = uiState.copy(isDeletingComponent = true)
            
            when (val result = bicycleRepository.deleteComponent(componentId)) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isDeletingComponent = false,
                        showDeleteComponentDialog = false,
                        deletingComponent = null,
                        showSuccessMessage = true,
                        successMessage = "Componente eliminado con éxito"
                    )
                    // Reload bicycle to get updated components
                    loadBicycle(bicycleId)
                }
                is BicycleResult.Error -> {
                    uiState = uiState.copy(
                        isDeletingComponent = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    // Kilometers dialog management
    fun showAddKilometersDialog() {
        uiState = uiState.copy(showAddKilometersDialog = true)
    }

    fun hideAddKilometersDialog() {
        uiState = uiState.copy(showAddKilometersDialog = false)
    }

    fun showSubtractKilometersDialog() {
        uiState = uiState.copy(showSubtractKilometersDialog = true)
    }

    fun hideSubtractKilometersDialog() {
        uiState = uiState.copy(showSubtractKilometersDialog = false)
    }

    // Kilometers operations
    fun addKilometers(kilometers: Double) {
        val bicycleId = uiState.bicycle?.id ?: return
        
        viewModelScope.launch {
            uiState = uiState.copy(isAddingKilometers = true)
            
            when (val result = bicycleRepository.addKilometers(bicycleId, kilometers)) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isAddingKilometers = false,
                        showAddKilometersDialog = false,
                        showSuccessMessage = true,
                        successMessage = "Kilómetros añadidos con éxito"
                    )
                    // Reload bicycle to get updated data
                    loadBicycle(bicycleId)
                }
                is BicycleResult.Error -> {
                    uiState = uiState.copy(
                        isAddingKilometers = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun subtractKilometers(kilometers: Double) {
        val bicycleId = uiState.bicycle?.id ?: return
        
        viewModelScope.launch {
            uiState = uiState.copy(isSubtractingKilometers = true)
            
            when (val result = bicycleRepository.subtractKilometers(bicycleId, kilometers)) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isSubtractingKilometers = false,
                        showSubtractKilometersDialog = false,
                        showSuccessMessage = true,
                        successMessage = "Kilómetros restados con éxito"
                    )
                    // Reload bicycle to get updated data
                    loadBicycle(bicycleId)
                }
                is BicycleResult.Error -> {
                    uiState = uiState.copy(
                        isSubtractingKilometers = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
} 