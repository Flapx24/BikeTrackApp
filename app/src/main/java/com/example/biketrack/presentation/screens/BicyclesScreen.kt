package com.example.biketrack.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biketrack.core.di.DependencyProvider
import com.example.biketrack.domain.entities.BicycleSummary
import com.example.biketrack.presentation.components.BicycleCard
import com.example.biketrack.presentation.components.CreateBicycleDialog
import com.example.biketrack.presentation.components.EditBicycleDialog
import com.example.biketrack.presentation.components.DeleteBicycleDialog
import com.example.biketrack.presentation.viewmodels.BicyclesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BicyclesScreen(
    onNavigateToBicycleDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val bicyclesViewModel: BicyclesViewModel = viewModel { 
        DependencyProvider.provideBicyclesViewModel() 
    }
    
    val uiState by remember { derivedStateOf { bicyclesViewModel.uiState } }
    
    // Handle error messages
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            // Error will be shown in the UI
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Bicicletas",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Refresh button
                    IconButton(
                        onClick = { bicyclesViewModel.loadBicycles() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar lista"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
            
            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        // Loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Cargando bicicletas...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    uiState.errorMessage != null -> {
                        // Error state
                        val errorMessage = uiState.errorMessage
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    text = "Error al cargar las bicicletas",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = errorMessage ?: "Error desconocido",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = { 
                                        bicyclesViewModel.clearError()
                                        bicyclesViewModel.loadBicycles()
                                    }
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                    
                    uiState.bicycles.isEmpty() -> {
                        // Empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    text = "No tienes bicicletas registradas",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Agrega tu primera bicicleta para comenzar a gestionar su mantenimiento y componentes.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = { bicyclesViewModel.showCreateDialog() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Agregar Bicicleta")
                                }
                            }
                        }
                    }
                    
                    else -> {
                        // Bicycles list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.bicycles,
                                key = { bicycle -> bicycle.id }
                            ) { bicycle ->
                                BicycleCard(
                                    bicycle = bicycle,
                                    onClick = { clickedBicycle ->
                                        onNavigateToBicycleDetail(clickedBicycle.id)
                                    },
                                    onEditClick = { editBicycle ->
                                        bicyclesViewModel.showEditDialog(editBicycle)
                                    },
                                    onDeleteClick = { deleteBicycle ->
                                        bicyclesViewModel.showDeleteDialog(deleteBicycle)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Floating Action Button for creating new bicycle
        if (!uiState.bicycles.isEmpty()) {
            FloatingActionButton(
                onClick = { bicyclesViewModel.showCreateDialog() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar bicicleta",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
    
    // Dialogs
    if (uiState.showCreateDialog) {
        CreateBicycleDialog(
            onDismiss = { bicyclesViewModel.hideCreateDialog() },
            onConfirm = { name, totalKilometers, lastMaintenanceDate ->
                bicyclesViewModel.createBicycle(name, totalKilometers, lastMaintenanceDate)
            },
            isLoading = uiState.isProcessing
        )
    }
    
    uiState.editingBicycle?.let { editingBicycle ->
        if (uiState.showEditDialog) {
            EditBicycleDialog(
                bicycle = editingBicycle,
                onDismiss = { bicyclesViewModel.hideEditDialog() },
                onConfirm = { bicycleId, name, totalKilometers, lastMaintenanceDate ->
                    bicyclesViewModel.updateBicycle(bicycleId, name, totalKilometers, lastMaintenanceDate)
                },
                isLoading = uiState.isProcessing
            )
        }
    }
    
    uiState.deletingBicycle?.let { deletingBicycle ->
        if (uiState.showDeleteDialog) {
            DeleteBicycleDialog(
                bicycle = deletingBicycle,
                onDismiss = { bicyclesViewModel.hideDeleteDialog() },
                onConfirm = {
                    bicyclesViewModel.deleteBicycle(deletingBicycle.id)
                },
                isLoading = uiState.isProcessing
            )
        }
    }
} 