package com.example.biketrack.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biketrack.core.session.SessionManager
import com.example.biketrack.domain.entities.Route
import com.example.biketrack.domain.entities.Review
import com.example.biketrack.domain.entities.RouteUpdate
import com.example.biketrack.domain.repositories.RouteResult
import com.example.biketrack.domain.usecases.route.*
import kotlinx.coroutines.launch

data class RouteDetailUiState(
    val isLoading: Boolean = false,
    val route: Route? = null,
    val reviews: List<Review> = emptyList(),
    val currentUserReview: Review? = null,
    val routeUpdates: List<RouteUpdate> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoadingReviews: Boolean = false,
    val isLoadingMoreReviews: Boolean = false,
    val hasMoreReviews: Boolean = true,
    val isCreatingReview: Boolean = false,
    val isUpdatingReview: Boolean = false,
    val isDeletingReview: Boolean = false,
    val isLoadingUpdates: Boolean = false,
    val showCreateReviewDialog: Boolean = false,
    val showEditReviewDialog: Boolean = false,
    val showDeleteReviewDialog: Boolean = false,
    val currentImageIndex: Int = 0
)

class RouteDetailViewModel(
    private val getRouteByIdUseCase: GetRouteByIdUseCase,
    private val getRouteReviewsUseCase: GetRouteReviewsUseCase,
    private val manageReviewUseCase: ManageReviewUseCase
) : ViewModel() {
    
    var uiState by mutableStateOf(RouteDetailUiState())
        private set
    
    private var routeId: Long = 0
    
    fun loadRouteDetails(routeId: Long) {
        this.routeId = routeId
        
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            
            when (val result = getRouteByIdUseCase(routeId)) {
                is RouteResult.Success -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        route = result.data
                    )

                    loadCurrentUserReview()

                    loadReviews()
                }
                is RouteResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    fun loadReviews(isLoadMore: Boolean = false) {
        if (uiState.isLoadingReviews || (isLoadMore && !uiState.hasMoreReviews)) return
        
        viewModelScope.launch {
            if (isLoadMore) {
                uiState = uiState.copy(isLoadingMoreReviews = true)
            } else {
                uiState = uiState.copy(isLoadingReviews = true, reviews = emptyList())
            }
            
            val lastReviewId = if (isLoadMore && uiState.reviews.isNotEmpty()) {
                uiState.reviews.last().id
            } else {
                null
            }
            
            when (val result = getRouteReviewsUseCase(routeId, lastReviewId)) {
                is RouteResult.Success -> {
                    val newReviews = result.data
                    uiState = if (isLoadMore) {
                        uiState.copy(
                            isLoadingMoreReviews = false,
                            reviews = uiState.reviews + newReviews,
                            hasMoreReviews = newReviews.isNotEmpty()
                        )
                    } else {
                        uiState.copy(
                            isLoadingReviews = false,
                            reviews = newReviews,
                            hasMoreReviews = newReviews.isNotEmpty()
                        )
                    }
                }
                is RouteResult.Error -> {
                    uiState = uiState.copy(
                        isLoadingReviews = false,
                        isLoadingMoreReviews = false,
                        reviews = emptyList(),
                        hasMoreReviews = false
                        // Don't show error message for reviews loading to avoid disrupting the main route view
                    )
                }
            }
        }
    }
    
    private fun loadReviewsSafely() {
        viewModelScope.launch {
            try {
                when (val result = getRouteReviewsUseCase(routeId, null)) {
                    is RouteResult.Success -> {
                        uiState = uiState.copy(reviews = result.data)
                    }
                    is RouteResult.Error -> {
                        // Don't show error for review loading after creating/updating/deleting
                        // Just keep the current reviews list
                    }
                }
            } catch (e: Exception) {
                // Silently handle any parsing errors to avoid breaking the UI
            }
        }
    }
    
    private fun loadCurrentUserReview() {
        viewModelScope.launch {
            try {
                when (val result = manageReviewUseCase.getCurrentUserReview(routeId)) {
                    is RouteResult.Success -> {
                        uiState = uiState.copy(currentUserReview = result.data)
                    }
                    is RouteResult.Error -> {
                        // Current user doesn't have a review, which is normal
                        uiState = uiState.copy(currentUserReview = null)
                    }
                }
            } catch (e: Exception) {
                // Silently handle any errors to avoid affecting the main route view
                uiState = uiState.copy(currentUserReview = null)
            }
        }
    }
    

    
    fun createReview(rating: Int, text: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isCreatingReview = true)
            
            when (val result = manageReviewUseCase.createReview(routeId, text, rating)) {
                is RouteResult.Success -> {
                    uiState = uiState.copy(
                        isCreatingReview = false,
                        currentUserReview = result.data,
                        showCreateReviewDialog = false,
                        successMessage = "Reseña creada con éxito"
                    )
                    // Reload the entire route to update review count and average score
                    reloadRouteDetails()
                    // Reload reviews to show the new one, but handle errors gracefully
                    loadReviewsSafely()
                }
                is RouteResult.Error -> {
                    uiState = uiState.copy(
                        isCreatingReview = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    fun updateReview(rating: Int, text: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isUpdatingReview = true)
            
            when (val result = manageReviewUseCase.updateReview(routeId, text, rating)) {
                is RouteResult.Success -> {
                    uiState = uiState.copy(
                        isUpdatingReview = false,
                        currentUserReview = result.data,
                        showEditReviewDialog = false,
                        successMessage = "Reseña actualizada con éxito"
                    )
                    // Reload the entire route to update review count and average score
                    reloadRouteDetails()
                    // Reload reviews to show the updated one
                    loadReviewsSafely()
                }
                is RouteResult.Error -> {
                    uiState = uiState.copy(
                        isUpdatingReview = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    fun deleteReview() {
        viewModelScope.launch {
            uiState = uiState.copy(isDeletingReview = true)
            
            when (val result = manageReviewUseCase.deleteReview(routeId)) {
                is RouteResult.Success -> {
                    uiState = uiState.copy(
                        isDeletingReview = false,
                        currentUserReview = null,
                        showDeleteReviewDialog = false,
                        successMessage = "Reseña eliminada con éxito"
                    )
                    // Reload the entire route to update review count and average score
                    reloadRouteDetails()
                    // Reload reviews to remove the deleted one
                    loadReviewsSafely()
                }
                is RouteResult.Error -> {
                    uiState = uiState.copy(
                        isDeletingReview = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
    
    fun showCreateReviewDialog() {
        // Ensure all other dialogs are closed first
        uiState = uiState.copy(
            showCreateReviewDialog = true,
            showEditReviewDialog = false,
            showDeleteReviewDialog = false
        )
    }
    
    fun hideCreateReviewDialog() {
        uiState = uiState.copy(
            showCreateReviewDialog = false,
            isCreatingReview = false
        )
    }
    
    fun showEditReviewDialog() {
        // Ensure all other dialogs are closed first
        uiState = uiState.copy(
            showEditReviewDialog = true,
            showCreateReviewDialog = false,
            showDeleteReviewDialog = false
        )
    }
    
    fun hideEditReviewDialog() {
        uiState = uiState.copy(
            showEditReviewDialog = false,
            isUpdatingReview = false
        )
    }
    
    fun showDeleteReviewDialog() {
        // Ensure all other dialogs are closed first
        uiState = uiState.copy(
            showDeleteReviewDialog = true,
            showCreateReviewDialog = false,
            showEditReviewDialog = false
        )
    }
    
    fun hideDeleteReviewDialog() {
        uiState = uiState.copy(
            showDeleteReviewDialog = false,
            isDeletingReview = false
        )
    }
    
    fun updateImageIndex(index: Int) {
        uiState = uiState.copy(currentImageIndex = index)
    }
    
    fun nextImage() {
        val route = uiState.route ?: return
        if (route.imageUrls.isNotEmpty()) {
            val newIndex = (uiState.currentImageIndex + 1).coerceAtMost(route.imageUrls.size - 1)
            uiState = uiState.copy(currentImageIndex = newIndex)
        }
    }
    
    fun previousImage() {
        val newIndex = (uiState.currentImageIndex - 1).coerceAtLeast(0)
        uiState = uiState.copy(currentImageIndex = newIndex)
    }
    
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
    
    fun clearSuccessMessage() {
        uiState = uiState.copy(successMessage = null)
    }
    
    fun retryLoading() {
        if (routeId > 0) {
            loadRouteDetails(routeId)
        }
    }
    
    fun isCurrentUserReview(review: Review): Boolean {
        val currentUserId = SessionManager.getUserId()
        return currentUserId == review.user.id
    }
    
    private fun reloadRouteDetails() {
        viewModelScope.launch {
            when (val result = getRouteByIdUseCase(routeId)) {
                is RouteResult.Success -> {
                    uiState = uiState.copy(route = result.data)
                }
                is RouteResult.Error -> {
                    // Don't show error as this is a background refresh
                }
            }
        }
    }
} 