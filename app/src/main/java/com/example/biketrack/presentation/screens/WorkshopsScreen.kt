package com.example.biketrack.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biketrack.core.di.DependencyProvider
import com.example.biketrack.presentation.components.ImageCarousel
import com.example.biketrack.presentation.components.OSMMapView
import com.example.biketrack.presentation.components.WorkshopCard
import com.example.biketrack.presentation.viewmodels.WorkshopsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopsScreen(
    modifier: Modifier = Modifier
) {
    val workshopsViewModel: WorkshopsViewModel = viewModel { 
        DependencyProvider.provideWorkshopsViewModel() 
    }
    val uiState by remember { derivedStateOf { workshopsViewModel.uiState } }
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var shouldRequestFocus by remember { mutableStateOf(false) }
    
    // Handle focus management after clearing field
    LaunchedEffect(shouldRequestFocus) {
        if (shouldRequestFocus) {
            focusRequester.requestFocus()
            shouldRequestFocus = false
        }
    }
    
    // Monitor scroll position for "scroll to top" button
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisibleItemIndex ->
                workshopsViewModel.updateScrollToTopButtonVisibility(firstVisibleItemIndex > 2)
            }
    }
    
    // Show errors in snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            delay(100)
            workshopsViewModel.clearError()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.showMap) {
            // Map view - full screen
            OSMMapView(
                workshops = uiState.workshops,
                selectedWorkshop = uiState.selectedWorkshop,
                onWorkshopClick = workshopsViewModel::selectWorkshop,
                onCloseMap = { workshopsViewModel.hideMap() },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // List view
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Buscar talleres por ciudad",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = uiState.searchCity,
                                onValueChange = { newValue ->
                                    val wasEmpty = uiState.searchCity.isNotEmpty()
                                    workshopsViewModel.updateSearchCity(newValue)
                                    // Si se borró todo el texto manualmente, mantener focus
                                    if (wasEmpty && newValue.isEmpty()) {
                                        shouldRequestFocus = true
                                    }
                                },
                                label = { Text("Ciudad") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Buscar"
                                    )
                                },
                                trailingIcon = if (uiState.searchCity.isNotBlank()) {
                                    {
                                        IconButton(
                                            onClick = { 
                                                workshopsViewModel.clearSearchField()
                                                // Activar flag para solicitar focus después de recomposición
                                                shouldRequestFocus = true
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Limpiar"
                                            )
                                        }
                                    }
                                } else null,
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                enabled = !uiState.isLoading
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    workshopsViewModel.searchWorkshops()
                                },
                                enabled = !uiState.isLoading && uiState.searchCity.isNotBlank()
                            ) {
                                Text("Buscar")
                            }
                        }
                    }
                }
                
                // Content
                when {
                    !uiState.hasSearched -> {
                        // Initial state - ask to search
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = "Busca talleres por ciudad",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Introduce el nombre de una ciudad para encontrar talleres de bicicletas cercanos",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
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
                                    text = "Buscando talleres...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    uiState.workshops.isEmpty() -> {
                        // No results state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    text = "No se encontraron talleres",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "No hay talleres disponibles en \"${uiState.lastSearchedCity}\". Intenta con otra ciudad.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    else -> {
                        // Workshops list
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.workshops,
                                key = { workshop -> workshop.id }
                            ) { workshop ->
                                WorkshopCard(
                                    workshop = workshop,
                                    onWorkshopClick = workshopsViewModel::onWorkshopCardClick
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Floating Action Button for map (only when search has results)
        if (!uiState.showMap && uiState.hasSearched && uiState.workshops.isNotEmpty()) {
            FloatingActionButton(
                onClick = { workshopsViewModel.showMap() },
                modifier = Modifier
                    .align(if (uiState.showScrollToTopButton) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Ver en mapa"
                )
            }
        }
        
        // Scroll to top button
        if (!uiState.showMap && uiState.showScrollToTopButton) {
            val coroutineScope = rememberCoroutineScope()
            
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Volver al inicio"
                )
            }
        }
        
        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Workshop detail modal
    if (uiState.showWorkshopModal && uiState.selectedWorkshop != null) {
        val workshop = uiState.selectedWorkshop!!
        
        Dialog(
            onDismissRequest = { workshopsViewModel.hideWorkshopModal() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = workshop.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        
                        TextButton(
                            onClick = { workshopsViewModel.hideWorkshopModal() }
                        ) {
                            Text("Cerrar")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Image carousel
                    ImageCarousel(
                        imageUrls = workshop.imageUrls,
                        currentIndex = uiState.currentImageIndex,
                        onIndexChange = workshopsViewModel::updateCurrentImageIndex,
                        onPreviousClick = workshopsViewModel::previousImage,
                        onNextClick = workshopsViewModel::nextImage,
                        modifier = Modifier.fillMaxWidth(),
                        aspectRatio = 16f / 9f
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Address
                    Text(
                        text = "Dirección:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = workshop.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
} 