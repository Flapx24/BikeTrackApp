package com.example.biketrack.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.biketrack.core.config.ApiConfig
import com.example.biketrack.domain.entities.Review
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReviewCard(
    review: Review,
    isCurrentUserReview: Boolean = false,
    onEditClick: ((Review) -> Unit)? = null,
    onDeleteClick: ((Review) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 20.dp)
        ) {
            // Header with user info and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User avatar
                val imageUrl = review.user.imageUrl?.let { imagePath ->
                    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                        imagePath
                    } else {
                        val baseUrl = ApiConfig.BASE_URL
                            .removeSuffix("/")
                            .removeSuffix("/api")
                        val path = if (imagePath.startsWith("/")) imagePath else "/$imagePath"
                        "$baseUrl$path"
                    }
                }
                
                var imageLoadState by remember(imageUrl) { mutableStateOf<AsyncImagePainter.State?>(null) }
                var hasTriedToLoad by remember(imageUrl) { mutableStateOf(false) }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUrl != null && (!hasTriedToLoad || imageLoadState !is AsyncImagePainter.State.Error)) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Avatar de ${review.user.username}",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            onState = { state -> 
                                imageLoadState = state
                                if (state is AsyncImagePainter.State.Error || state is AsyncImagePainter.State.Success) {
                                    hasTriedToLoad = true
                                }
                            }
                        )
                    }
                    
                    // Show fallback icon when no image URL or when image fails to load
                    if (imageUrl == null || (hasTriedToLoad && imageLoadState is AsyncImagePainter.State.Error)) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // User info and date
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = review.user.username,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = review.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Action buttons for current user's review
                if (isCurrentUserReview && (onEditClick != null || onDeleteClick != null)) {
                    Row {
                        onEditClick?.let { editAction ->
                            IconButton(
                                onClick = { editAction(review) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar reseña",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        onDeleteClick?.let { deleteAction ->
                            IconButton(
                                onClick = { deleteAction(review) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar reseña",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Star rating
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    val isFilled = index < review.rating
                    
                    Icon(
                        imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (isFilled) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "${review.rating}/5",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Review text
            if (!review.text.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = review.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
} 