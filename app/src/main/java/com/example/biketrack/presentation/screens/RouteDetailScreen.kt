package com.example.biketrack.presentation.screens

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biketrack.core.di.DependencyProvider
import com.example.biketrack.domain.entities.Difficulty
import com.example.biketrack.presentation.components.*
import com.example.biketrack.presentation.viewmodels.RouteDetailViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    routeId: Long,
    onBackClick: () -> Unit,
    onNavigateToUpdates: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: RouteDetailViewModel = viewModel { 
        DependencyProvider.provideRouteDetailViewModel() 
    }
    val uiState by remember { derivedStateOf { viewModel.uiState } }
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var isMapBeingInteracted by remember { mutableStateOf(false) }
    
    // Load route details on first composition
    LaunchedEffect(routeId) {
        viewModel.loadRouteDetails(routeId)
    }
    
    // Load more reviews when reaching near the end
    LaunchedEffect(listState) {
        snapshotFlow { 
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index 
        }.collect { lastVisibleIndex ->
            if (lastVisibleIndex != null && 
                lastVisibleIndex >= uiState.reviews.size - 3 && 
                uiState.hasMoreReviews && 
                !uiState.isLoadingMoreReviews && 
                !uiState.isLoadingReviews) {
                viewModel.loadReviews(isLoadMore = true)
            }
        }
    }
    
    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            delay(100)
            viewModel.clearError()
        }
    }
    
    // Show success messages
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            delay(100)
            viewModel.clearSuccessMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.route?.title ?: "Detalle de Ruta",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Notifications bell for route updates with badge
                    uiState.route?.let { route ->
                        Box {
                            IconButton(
                                onClick = { onNavigateToUpdates(route.id, route.title) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Ver actualizaciones de la ruta",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            // Badge for update count
                            if (route.updateCount > 0) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp)
                                ) {
                                    Text(
                                        text = if (route.updateCount > 9) "9+" else route.updateCount.toString(),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        
        when {
            uiState.isLoading -> {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
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
                            text = "Cargando detalles de la ruta...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            uiState.route == null -> {
                // Error state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "No se pudo cargar la ruta",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Verifica tu conexión e intenta nuevamente",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { viewModel.retryLoading() }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            
            else -> {
                // Content with image carousel, route info, map, and reviews
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    userScrollEnabled = !isMapBeingInteracted,
                    reverseLayout = false
                ) {
                    val route = uiState.route!!
                    
                    // Image carousel
                    item {
                        ImageCarousel(
                            imageUrls = route.imageUrls,
                            currentIndex = uiState.currentImageIndex,
                            onIndexChange = viewModel::updateImageIndex,
                            onPreviousClick = viewModel::previousImage,
                            onNextClick = viewModel::nextImage,
                            modifier = Modifier.padding(16.dp),
                            aspectRatio = 16f / 9f
                        )
                    }
                    
                    // Route title
                    item {
                        Text(
                            text = route.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    
                    // Route information
                    item {
                        RouteInformationCard(
                            route = route,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Map
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.mapInteractionHandler { interacting ->
                                    isMapBeingInteracted = interacting
                                }
                            ) {
                                RouteMapView(
                                    calculatedRoutePoints = route.calculatedRoutePoints,
                                    routePoints = route.routePoints,
                                    routeTitle = route.title
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    // Reviews section
                    item {
                        ReviewsSectionHeader(
                            reviewCount = route.reviewCount,
                            currentUserReview = uiState.currentUserReview,
                            onCreateReviewClick = viewModel::showCreateReviewDialog,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Show loading or reviews
                    if (uiState.isLoadingReviews) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else if (uiState.reviews.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay reseñas disponibles",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(
                            items = uiState.reviews,
                            key = { review -> "review_${review.id}" },
                            contentType = { "review" }
                        ) { review ->
                            val isCurrentUserReview = viewModel.isCurrentUserReview(review)
                            
                            ReviewCard(
                                review = review,
                                isCurrentUserReview = isCurrentUserReview,
                                onEditClick = if (isCurrentUserReview) { _ -> 
                                    viewModel.showEditReviewDialog() 
                                } else null,
                                onDeleteClick = if (isCurrentUserReview) { _ -> 
                                    viewModel.showDeleteReviewDialog() 
                                } else null,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                        
                        // Loading more indicator
                        if (uiState.isLoadingMoreReviews) {
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
                                            text = "Cargando más reseñas...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Review dialogs
        if (uiState.showCreateReviewDialog) {
            CreateReviewDialog(
                onDismiss = viewModel::hideCreateReviewDialog,
                onConfirm = { rating, text ->
                    viewModel.createReview(rating, text)
                },
                isLoading = uiState.isCreatingReview
            )
        }
        
        if (uiState.showEditReviewDialog && uiState.currentUserReview != null) {
            EditReviewDialog(
                review = uiState.currentUserReview!!,
                onDismiss = viewModel::hideEditReviewDialog,
                onConfirm = { rating, text ->
                    viewModel.updateReview(rating, text)
                },
                isLoading = uiState.isUpdatingReview
            )
        }
        
        if (uiState.showDeleteReviewDialog) {
            DeleteReviewDialog(
                onDismiss = viewModel::hideDeleteReviewDialog,
                onConfirm = viewModel::deleteReview,
                isLoading = uiState.isDeletingReview
            )
        }
    }
}

@Composable
private fun RouteInformationCard(
    route: com.example.biketrack.domain.entities.Route,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Description
            if (route.description.isNotBlank()) {
                Text(
                    text = route.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Route stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = route.city.capitalizeFirstLetter(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dificultad: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = when (route.difficulty) {
                                Difficulty.EASY -> "Fácil"
                                Difficulty.MEDIUM -> "Media"
                                Difficulty.HARD -> "Difícil"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (route.difficulty) {
                                Difficulty.EASY -> MaterialTheme.colorScheme.primary
                                Difficulty.MEDIUM -> Color(0xFFFFB300)
                                Difficulty.HARD -> MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    route.calculatedTotalDistanceKm?.let { distance ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Distancia: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$distance km",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    route.calculatedEstimatedTimeMinutes?.let { time ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Duración: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$time min",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Rating
            RatingDisplay(
                rating = route.averageReviewScore,
                reviewCount = route.reviewCount
            )
        }
    }
}

@Composable
private fun ReviewsSectionHeader(
    reviewCount: Int,
    currentUserReview: com.example.biketrack.domain.entities.Review?,
    onCreateReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Reseñas ($reviewCount)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        if (currentUserReview == null) {
            Button(
                onClick = onCreateReviewClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Reseña")
            }
        }
    }
}

private fun String.capitalizeFirstLetter(): String {
    return if (this.isNotEmpty()) {
        this.lowercase(Locale.getDefault()).replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    } else {
        this
    }
}

/**
 * Modifier extension that tracks map interaction without consuming events
 * This allows all gestures to work on the map while disabling parent scroll
 */
private fun Modifier.mapInteractionHandler(
    onInteractionChange: (Boolean) -> Unit
): Modifier = this.pointerInput(Unit) {
    awaitEachGesture {
        // Wait for first touch down - don't consume it, let it pass to the map
        awaitFirstDown(requireUnconsumed = false)
        
        // Notify that interaction started
        onInteractionChange(true)
        
        // Track the gesture without consuming any events
        do {
            val event = awaitPointerEvent()
            // Don't consume any events - let all gestures pass through to the map
        } while (event.changes.any { it.pressed })
        
        // Notify that interaction ended
        onInteractionChange(false)
    }
} 