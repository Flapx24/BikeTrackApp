package com.example.biketrack.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.domain.entities.RouteUpdate
import com.example.biketrack.domain.entities.UpdateType
import com.example.biketrack.domain.usecases.route.GetRouteUpdatesUseCase
import com.example.biketrack.domain.usecases.route.ManageRouteUpdatesUseCase
import com.example.biketrack.domain.repositories.RouteResult
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RouteUpdatesUiState(
    val isLoading: Boolean = false,
    val updates: List<RouteUpdate> = emptyList(),
    val error: String? = null,
    val isCreating: Boolean = false,
    val isUpdating: Boolean = false,
    val isDeleting: Boolean = false,
    val showSuccessMessage: Boolean = false,
    val successMessage: String = ""
)

class RouteUpdatesViewModel(
    private val getRouteUpdatesUseCase: GetRouteUpdatesUseCase,
    private val manageRouteUpdatesUseCase: ManageRouteUpdatesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RouteUpdatesUiState())
    val uiState: StateFlow<RouteUpdatesUiState> = _uiState.asStateFlow()
    
    private var currentRouteId: Long = 0
    
    fun loadRouteUpdates(routeId: Long) {
        if (routeId <= 0) return
        
        currentRouteId = routeId
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )
        
        viewModelScope.launch {
            when (val result = getRouteUpdatesUseCase(routeId)) {
                is RouteResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        updates = result.data.sortedByDescending { it.date },
                        error = null
                    )
                }
                is RouteResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    fun createRouteUpdate(
        routeId: Long,
        description: String,
        date: LocalDate,
        type: UpdateType,
        resolved: Boolean
    ) {
        if (routeId <= 0) {
            _uiState.value = _uiState.value.copy(error = "No hay ruta seleccionada")
            return
        }
        
        _uiState.value = _uiState.value.copy(isCreating = true)
        
        viewModelScope.launch {
            when (val result = manageRouteUpdatesUseCase.createRouteUpdate(
                routeId = routeId,
                description = description,
                date = date,
                type = type,
                resolved = resolved
            )) {
                is RouteResult.Success -> {
                    // Reload the updates list
                    loadRouteUpdates(routeId)
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        showSuccessMessage = true,
                        successMessage = "Actualización creada correctamente"
                    )
                }
                is RouteResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    fun updateRouteUpdate(
        id: Long,
        description: String,
        date: LocalDate,
        type: UpdateType,
        resolved: Boolean
    ) {
        _uiState.value = _uiState.value.copy(isUpdating = true)
        
        viewModelScope.launch {
            when (val result = manageRouteUpdatesUseCase.updateRouteUpdate(
                id = id,
                description = description,
                date = date,
                type = type,
                resolved = resolved
            )) {
                is RouteResult.Success -> {
                    // Update the specific item in the list
                    val updatedList = _uiState.value.updates.map { update ->
                        if (update.id == id) result.data else update
                    }.sortedByDescending { it.date }
                    
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        updates = updatedList,
                        showSuccessMessage = true,
                        successMessage = "Actualización modificada correctamente"
                    )
                }
                is RouteResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isUpdating = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    fun deleteRouteUpdate(routeUpdateId: Long) {
        _uiState.value = _uiState.value.copy(isDeleting = true)
        
        viewModelScope.launch {
            when (val result = manageRouteUpdatesUseCase.deleteRouteUpdate(routeUpdateId)) {
                is RouteResult.Success -> {
                    // Remove the item from the list
                    val updatedList = _uiState.value.updates.filter { it.id != routeUpdateId }
                    
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        updates = updatedList,
                        showSuccessMessage = true,
                        successMessage = "Actualización eliminada correctamente"
                    )
                }
                is RouteResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        error = result.message
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(
            showSuccessMessage = false,
            successMessage = ""
        )
    }
    
    fun getCurrentUserId(): Long? {
        return SessionManager.getUserId()
    }
} 