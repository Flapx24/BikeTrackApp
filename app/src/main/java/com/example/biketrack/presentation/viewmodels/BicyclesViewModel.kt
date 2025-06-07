package com.example.biketrack.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biketrack.domain.entities.BicycleSummary
import com.example.biketrack.domain.repositories.BicycleResult
import com.example.biketrack.domain.usecases.bicycle.GetUserBicyclesUseCase
import com.example.biketrack.domain.usecases.bicycle.GetBicycleByIdUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate

class BicyclesViewModel(
    private val getUserBicyclesUseCase: GetUserBicyclesUseCase,
    private val getBicycleByIdUseCase: GetBicycleByIdUseCase,
    private val bicycleRepository: com.example.biketrack.domain.repositories.BicycleRepository
) : ViewModel() {

    var uiState by mutableStateOf(BicyclesUiState())
        private set

    init {
        loadBicycles()
    }

    fun loadBicycles() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            
            when (val result = getUserBicyclesUseCase()) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        bicycles = result.data,
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

    fun showCreateDialog() {
        uiState = uiState.copy(showCreateDialog = true)
    }

    fun hideCreateDialog() {
        uiState = uiState.copy(showCreateDialog = false)
    }

    fun showEditDialog(bicycle: BicycleSummary) {
        uiState = uiState.copy(
            showEditDialog = true,
            editingBicycle = bicycle
        )
    }

    fun hideEditDialog() {
        uiState = uiState.copy(
            showEditDialog = false,
            editingBicycle = null
        )
    }

    fun showDeleteDialog(bicycle: BicycleSummary) {
        uiState = uiState.copy(
            showDeleteDialog = true,
            deletingBicycle = bicycle
        )
    }

    fun hideDeleteDialog() {
        uiState = uiState.copy(
            showDeleteDialog = false,
            deletingBicycle = null
        )
    }

    fun createBicycle(
        name: String,
        totalKilometers: Double,
        lastMaintenanceDate: LocalDate?
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isProcessing = true)
            
            val dateString = lastMaintenanceDate?.toString()
            when (val result = bicycleRepository.createBicycle(name, null, totalKilometers, dateString)) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isProcessing = false,
                        showCreateDialog = false
                    )
                    loadBicycles() // Reload the list
                }
                is BicycleResult.Error -> {
                    uiState = uiState.copy(
                        isProcessing = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun updateBicycle(
        bicycleId: Long,
        name: String,
        totalKilometers: Double,
        lastMaintenanceDate: LocalDate?
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isProcessing = true)
            
            val dateString = lastMaintenanceDate?.toString()
            when (val result = bicycleRepository.updateBicycle(bicycleId, name, null, totalKilometers, dateString)) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isProcessing = false,
                        showEditDialog = false,
                        editingBicycle = null
                    )
                    loadBicycles() // Reload the list
                }
                is BicycleResult.Error -> {
                    uiState = uiState.copy(
                        isProcessing = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun deleteBicycle(bicycleId: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isProcessing = true)
            
            when (val result = bicycleRepository.deleteBicycle(bicycleId)) {
                is BicycleResult.Success -> {
                    uiState = uiState.copy(
                        isProcessing = false,
                        showDeleteDialog = false,
                        deletingBicycle = null
                    )
                    loadBicycles() // Reload the list
                }
                is BicycleResult.Error -> {
                    uiState = uiState.copy(
                        isProcessing = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}

data class BicyclesUiState(
    val isLoading: Boolean = false,
    val bicycles: List<BicycleSummary> = emptyList(),
    val errorMessage: String? = null,
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val editingBicycle: BicycleSummary? = null,
    val deletingBicycle: BicycleSummary? = null,
    val isProcessing: Boolean = false
) 