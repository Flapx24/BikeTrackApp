package com.example.biketrack.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biketrack.core.di.DependencyProvider
import com.example.biketrack.domain.entities.Route
import com.example.biketrack.presentation.components.RouteCard
import com.example.biketrack.presentation.components.RouteFilters
import com.example.biketrack.presentation.viewmodels.RoutesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RoutesScreen(
    modifier: Modifier = Modifier
) {
    val routesViewModel: RoutesViewModel = viewModel { 
        DependencyProvider.provideRoutesViewModel() 
    }
    val uiState by remember { derivedStateOf { routesViewModel.uiState } }
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    
    // Monitor scroll position for "scroll to top" button
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisibleItemIndex ->
                routesViewModel.updateScrollToTopButtonVisibility(firstVisibleItemIndex > 2)
            }
    }
    
    // Load more data when reaching near the end
    LaunchedEffect(listState) {
        snapshotFlow { 
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index 
        }.collect { lastVisibleIndex ->
            if (lastVisibleIndex != null && 
                lastVisibleIndex >= uiState.routes.size - 3 && 
                uiState.hasMoreData && 
                !uiState.isLoadingMore && 
                !uiState.isLoading) {
                routesViewModel.loadRoutes(isLoadMore = true)
            }
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
            routesViewModel.clearError()
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Filters section
            RouteFilters(
                selectedCity = uiState.selectedCity,
                onCityChange = routesViewModel::updateSelectedCity,
                minRating = uiState.minRating,
                onMinRatingChange = routesViewModel::updateMinRating,
                onFilterApply = routesViewModel::filterRoutes,
                onFilterClear = routesViewModel::clearFilters,
                modifier = Modifier.padding(16.dp)
            )
            
            // Routes list
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
                                text = "Cargando rutas...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
                    }
                }
                
                uiState.routes.isEmpty() && !uiState.isLoading -> {
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
                                text = if (uiState.isFiltering) "No se encontraron rutas con los filtros aplicados" 
                                       else "No hay rutas disponibles",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = if (uiState.isFiltering) "Intenta ajustar los filtros de búsqueda" 
                                       else "Intenta recargar o vuelve más tarde",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            
                            if (!uiState.isFiltering) {
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = { routesViewModel.retryLoading() }
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                }
                
                else -> {
                    // Routes list
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.routes,
                            key = { route -> route.id }
                        ) { route ->
                            RouteCard(
                                route = route,
                                onRouteClick = { clickedRoute ->
                                    // TODO: Navigate to route details
                                }
                            )
                        }
                        
                        // Loading more indicator
                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Cargando más rutas...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        
                        // End of list indicator
                        if (!uiState.hasMoreData && uiState.routes.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Has visto todas las rutas disponibles",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Scroll to top button
        if (uiState.showScrollToTopButton) {
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
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
} 