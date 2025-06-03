package com.example.biketrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import android.util.Log
import android.util.Patterns
import com.example.biketrack.core.config.ApiConfig

private fun buildImageUrl(imagePath: String): String {
    return if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
        imagePath
    } else {
        val baseUrl = ApiConfig.BASE_URL
            .removeSuffix("/")
            .removeSuffix("/api")
        val path = if (imagePath.startsWith("/")) imagePath else "/$imagePath"
        "$baseUrl$path"
    }
}

private fun isValidUrl(url: String): Boolean {
    return Patterns.WEB_URL.matcher(url).matches() && 
           (url.startsWith("http://") || url.startsWith("https://"))
}

@Composable
fun ImageCarousel(
    imageUrls: List<String>,
    currentIndex: Int = 0,
    onIndexChange: (Int) -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    aspectRatio: Float = 16f / 9f
) {
    if (imageUrls.isEmpty()) {
        // No images state
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = "Sin imágenes",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sin imágenes disponibles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { imageUrls.size }
    )

    // Sync external index with pager state
    LaunchedEffect(currentIndex) {
        if (pagerState.currentPage != currentIndex) {
            pagerState.animateScrollToPage(currentIndex)
        }
    }

    // Notify external about page changes
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != currentIndex) {
            onIndexChange(pagerState.currentPage)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            var imageLoadState by remember(imageUrls[page]) { mutableStateOf<AsyncImagePainter.State?>(null) }
            var hasTriedToLoad by remember(imageUrls[page]) { mutableStateOf(false) }
            val fullImageUrl = buildImageUrl(imageUrls[page])
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (!hasTriedToLoad || imageLoadState !is AsyncImagePainter.State.Error) {
                AsyncImage(
                    model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(fullImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen ${page + 1} de ${imageUrls.size}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                        onState = { state -> 
                            imageLoadState = state
                            if (state is AsyncImagePainter.State.Error || state is AsyncImagePainter.State.Success) {
                                hasTriedToLoad = true
                            }
                        }
                )
                }
                
                // Loading state overlay
                when {
                    !hasTriedToLoad || imageLoadState is AsyncImagePainter.State.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    hasTriedToLoad && imageLoadState is AsyncImagePainter.State.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BrokenImage,
                                    contentDescription = "Error al cargar imagen",
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Imagen no disponible",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        // Navigation arrows (only show if more than 1 image)
        if (imageUrls.size > 1) {
            // Previous arrow
            if (currentIndex > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(8.dp)
                        .size(40.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onPreviousClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Imagen anterior",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Next arrow
            if (currentIndex < imageUrls.size - 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(8.dp)
                        .size(40.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onNextClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Imagen siguiente",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Image counter
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .background(
                    Color.Black.copy(alpha = 0.7f),
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${currentIndex + 1}/${imageUrls.size}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
} 