package com.example.biketrack.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biketrack.core.di.DependencyProvider
import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.domain.entities.RouteUpdate
import com.example.biketrack.domain.entities.UpdateType
import com.example.biketrack.presentation.components.RouteUpdateCard
import com.example.biketrack.presentation.components.CreateRouteUpdateDialog
import com.example.biketrack.presentation.components.EditRouteUpdateDialog
import com.example.biketrack.presentation.components.DeleteRouteUpdateDialog
import com.example.biketrack.presentation.viewmodels.RouteUpdatesViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteUpdatesScreen(
    routeId: Long,
    routeName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: RouteUpdatesViewModel = viewModel { 
        DependencyProvider.provideRouteUpdatesViewModel() 
    }
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId = SessionManager.getUserId()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var updateToEdit by remember { mutableStateOf<RouteUpdate?>(null) }
    var updateToDelete by remember { mutableStateOf<RouteUpdate?>(null) }
    
    // Load updates when screen is first displayed
    LaunchedEffect(routeId) {
        viewModel.loadRouteUpdates(routeId)
    }
    
    // Handle success messages
    LaunchedEffect(uiState.showSuccessMessage) {
        if (uiState.showSuccessMessage) {
            snackbarHostState.showSnackbar(
                message = uiState.successMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccessMessage()
        }
    }
    
    // Handle error messages
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Actualizaciones",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = routeName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir actualización",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
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
                                text = "Cargando actualizaciones...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                uiState.error != null -> {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error al cargar actualizaciones",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.error!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadRouteUpdates(routeId) }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                
                uiState.updates.isEmpty() -> {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay actualizaciones",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Sé el primero en añadir una actualización para esta ruta",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                else -> {
                    // Content with updates
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.updates) { update ->
                            val isCurrentUser = currentUserId == update.userId
                            
                            RouteUpdateCard(
                                routeUpdate = update,
                                isCurrentUserUpdate = isCurrentUser,
                                onEditClick = if (isCurrentUser) { { updateToEdit = it } } else null,
                                onDeleteClick = if (isCurrentUser) { { updateToDelete = it } } else null
                            )
                        }
                        
                        // Add extra space at the bottom for FAB
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
        
        // Dialogs
        if (showCreateDialog) {
            CreateRouteUpdateDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { description, date, type, resolved ->
                    viewModel.createRouteUpdate(routeId, description, date, type, resolved)
                    showCreateDialog = false
                },
                isLoading = uiState.isCreating
            )
        }
        
        updateToEdit?.let { update ->
            EditRouteUpdateDialog(
                routeUpdate = update,
                onDismiss = { updateToEdit = null },
                onConfirm = { description, date, type, resolved ->
                    viewModel.updateRouteUpdate(update.id, description, date, type, resolved)
                    updateToEdit = null
                },
                isLoading = uiState.isUpdating
            )
        }
        
        updateToDelete?.let { update ->
            DeleteRouteUpdateDialog(
                onDismiss = { updateToDelete = null },
                onConfirm = {
                    viewModel.deleteRouteUpdate(update.id)
                    updateToDelete = null
                },
                isLoading = uiState.isDeleting
            )
        }
    }
} 