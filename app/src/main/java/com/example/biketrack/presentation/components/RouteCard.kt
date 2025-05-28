package com.example.biketrack.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.biketrack.domain.entities.Difficulty
import com.example.biketrack.domain.entities.Route
import kotlin.math.round
import java.util.Locale

private fun String.capitalizeFirstLetter(): String {
    return if (this.isNotEmpty()) {
        this.lowercase(Locale.getDefault()).replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    } else {
        this
    }
}

@Composable
fun RouteCard(
    route: Route,
    onRouteClick: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onRouteClick(route) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title and difficulty
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = route.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                DifficultyChip(difficulty = route.difficulty)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // City
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Time and distance
                Row {
                    if (route.calculatedEstimatedTimeMinutes != null) {
                        StatItem(
                            icon = Icons.Default.Schedule,
                            value = "${route.calculatedEstimatedTimeMinutes} min"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    
                    if (route.calculatedTotalDistanceKm != null) {
                        StatItem(
                            icon = Icons.Default.Route,
                            value = "${route.calculatedTotalDistanceKm} km"
                        )
                    }
                }
                
                // Rating
                RatingDisplay(
                    rating = route.averageReviewScore,
                    reviewCount = route.reviewCount
                )
            }
            
            // Description (first line only if it doesn't take too much space)
            if (route.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = route.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun DifficultyChip(
    difficulty: Difficulty,
    modifier: Modifier = Modifier
) {
    val (text, icon, color) = when (difficulty) {
        Difficulty.EASY -> Triple("Fácil", Icons.Default.CheckCircle, MaterialTheme.colorScheme.primary)
        Difficulty.MEDIUM -> Triple("Media", Icons.Default.Terrain, Color(0xFFFFB300))
        Difficulty.HARD -> Triple("Difícil", Icons.Default.Warning, MaterialTheme.colorScheme.error)
    }
    
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = difficulty.name,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color,
            leadingIconContentColor = color
        )
    )
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RatingDisplay(
    rating: Double,
    reviewCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Star rating
        repeat(5) { index ->
            val isFilled = index < rating.toInt()
            val isHalfFilled = index == rating.toInt() && rating % 1 >= 0.5
            
            Icon(
                imageVector = when {
                    isFilled -> Icons.Default.Star
                    isHalfFilled -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Default.StarBorder
                },
                contentDescription = null,
                tint = if (isFilled || isHalfFilled) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = "${round(rating * 10) / 10} ($reviewCount)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 