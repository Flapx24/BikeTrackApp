package com.example.biketrack.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biketrack.core.di.DependencyProvider
import com.example.biketrack.domain.entities.BicycleComponent
import com.example.biketrack.presentation.components.*
import com.example.biketrack.presentation.viewmodels.BicycleDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BicycleDetailScreen(
    bicycleId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BicycleDetailViewModel = viewModel { DependencyProvider.provideBicycleDetailViewModel() }
) {
    val uiState = viewModel.uiState

    // Load bicycle data when screen opens
    LaunchedEffect(bicycleId) {
        viewModel.loadBicycle(bicycleId)
    }

    // Show success messages
    LaunchedEffect(uiState.showSuccessMessage) {
        if (uiState.showSuccessMessage) {
            // Auto-hide success message after 3 seconds
            kotlinx.coroutines.delay(3000)
            viewModel.clearSuccessMessage()
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
                        text = uiState.bicycle?.name ?: "Detalles de la Bicicleta",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

            // Content
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }
                uiState.errorMessage != null -> {
                    ErrorContent(
                        message = uiState.errorMessage,
                        onRetry = { viewModel.loadBicycle(bicycleId) },
                        onDismiss = { viewModel.clearError() }
                    )
                }
                uiState.bicycle != null -> {
                    BicycleDetailContent(
                        bicycle = uiState.bicycle,
                        viewModel = viewModel
                    )
                }
            }
        }

        // Floating Action Buttons for kilometers
        if (uiState.bicycle != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add kilometers FAB (top)
                FloatingActionButton(
                    onClick = { viewModel.showAddKilometersDialog() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir kilómetros",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                // Subtract kilometers FAB (bottom)
                FloatingActionButton(
                    onClick = { viewModel.showSubtractKilometersDialog() },
                    containerColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Restar kilómetros",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }

        // Success message snackbar
        if (uiState.showSuccessMessage) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.successMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.clearSuccessMessage() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (uiState.showCreateComponentDialog) {
        CreateComponentDialog(
            onDismiss = { viewModel.hideCreateComponentDialog() },
            onConfirm = { name, maxKm, currentKm ->
                viewModel.createComponent(name, maxKm, currentKm)
            },
            isLoading = uiState.isCreatingComponent
        )
    }

    if (uiState.showEditComponentDialog && uiState.editingComponent != null) {
        EditComponentDialog(
            component = uiState.editingComponent,
            onDismiss = { viewModel.hideEditComponentDialog() },
            onConfirm = { name, maxKm, currentKm ->
                viewModel.updateComponent(uiState.editingComponent.id, name, maxKm, currentKm)
            },
            isLoading = uiState.isUpdatingComponent
        )
    }

    if (uiState.showDeleteComponentDialog && uiState.deletingComponent != null) {
        DeleteComponentDialog(
            component = uiState.deletingComponent,
            onDismiss = { viewModel.hideDeleteComponentDialog() },
            onConfirm = { viewModel.deleteComponent(uiState.deletingComponent.id) },
            isLoading = uiState.isDeletingComponent
        )
    }

    if (uiState.showAddKilometersDialog) {
        AddKilometersDialog(
            onDismiss = { viewModel.hideAddKilometersDialog() },
            onConfirm = { kilometers -> viewModel.addKilometers(kilometers) },
            isLoading = uiState.isAddingKilometers
        )
    }

    if (uiState.showSubtractKilometersDialog) {
        SubtractKilometersDialog(
            onDismiss = { viewModel.hideSubtractKilometersDialog() },
            onConfirm = { kilometers -> viewModel.subtractKilometers(kilometers) },
            isLoading = uiState.isSubtractingKilometers
        )
    }
}

@Composable
private fun LoadingContent() {
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
                text = "Cargando detalles...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onRetry) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

@Composable
private fun BicycleDetailContent(
    bicycle: com.example.biketrack.domain.entities.Bicycle,
    viewModel: BicycleDetailViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = WindowInsets.navigationBars.asPaddingValues().let { navigationPadding ->
            PaddingValues(
                top = 16.dp,
                bottom = maxOf(140.dp, navigationPadding.calculateBottomPadding() + 140.dp), // Extra bottom padding for FABs + navigation area
                start = 0.dp,
                end = 0.dp
            )
        }
    ) {
        // Bicycle Info Card
        item {
            BicycleInfoCard(bicycle = bicycle)
        }

        // Components Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Componentes (${bicycle.components.size})",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Button(
                    onClick = { viewModel.showCreateComponentDialog() },
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Crear componente")
                }
            }
        }

        // Components List
        if (bicycle.components.isEmpty()) {
            item {
                EmptyComponentsCard()
            }
        } else {
            items(bicycle.components) { component ->
                ComponentCard(
                    component = component,
                    onEdit = { viewModel.showEditComponentDialog(component) },
                    onDelete = { viewModel.showDeleteComponentDialog(component) }
                )
            }
        }
    }
}

@Composable
private fun BicycleInfoCard(bicycle: com.example.biketrack.domain.entities.Bicycle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bicycle.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${bicycle.totalKilometers.toInt()} km totales",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.DirectionsBike,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Additional info
            Column {
                Text(
                    text = "Último mantenimiento:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = bicycle.lastMaintenanceDate?.let { 
                        it.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } ?: "Sin registro",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun EmptyComponentsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sin componentes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Agrega componentes para hacer seguimiento de su mantenimiento",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ComponentCard(
    component: BicycleComponent,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = component.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${component.currentKilometers.toInt()}/${component.maxKilometers.toInt()} km",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar componente",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar componente",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            val progress = (component.currentKilometers / component.maxKilometers).coerceIn(0.0, 1.0)
            val progressColor = when {
                progress >= 0.9 -> MaterialTheme.colorScheme.error
                progress >= 0.7 -> Color(0xFFFF9800) // Orange
                else -> MaterialTheme.colorScheme.primary
            }
            
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progreso de desgaste",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = progressColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = progress.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
} 