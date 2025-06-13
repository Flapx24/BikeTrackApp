package com.example.biketrack.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.biketrack.core.config.ApiConfig
import com.example.biketrack.domain.entities.BicycleSummary
import java.time.format.DateTimeFormatter
import java.util.Locale

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

@Composable
fun BicycleCard(
    bicycle: BicycleSummary,
    onClick: (BicycleSummary) -> Unit,
    onEditClick: ((BicycleSummary) -> Unit)? = null,
    onDeleteClick: ((BicycleSummary) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(bicycle) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Main content row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bicycle icon/image
                var imageLoadState by remember(bicycle.iconUrl) { mutableStateOf<AsyncImagePainter.State?>(null) }
                var hasTriedToLoad by remember(bicycle.iconUrl) { mutableStateOf(false) }
                
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (bicycle.iconUrl != null && (!hasTriedToLoad || imageLoadState !is AsyncImagePainter.State.Error)) {
                        val fullImageUrl = buildImageUrl(bicycle.iconUrl)
                        
                        // Background circle for the image
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(fullImageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Icono de ${bicycle.name}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit,
                                    onState = { state -> 
                                        imageLoadState = state
                                        if (state is AsyncImagePainter.State.Error || state is AsyncImagePainter.State.Success) {
                                            hasTriedToLoad = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                    
                    // Show fallback icon when no image URL or when image fails to load
                    if (bicycle.iconUrl == null || (hasTriedToLoad && imageLoadState is AsyncImagePainter.State.Error)) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBike,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    
                    // Loading state overlay
                    if (!hasTriedToLoad && imageLoadState is AsyncImagePainter.State.Loading) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Bicycle info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Bicycle name
                    Text(
                        text = bicycle.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Total kilometers
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Kilómetros",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${String.format("%.1f", bicycle.totalKilometers)} km",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Last maintenance date
                    if (bicycle.lastMaintenanceDate != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Último mantenimiento",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = bicycle.lastMaintenanceDate.format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Components count and maintenance status
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Components count
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "${bicycle.componentCount} componentes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Maintenance status
                        if (bicycle.needsMaintenance) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Necesita mantenimiento",
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Mantenimiento",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Arrow indicator
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver detalles",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Action buttons row (if edit or delete callbacks are provided)
            if (onEditClick != null || onDeleteClick != null) {
                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Edit button
                    onEditClick?.let { editAction ->
                        TextButton(
                            onClick = { editAction(bicycle) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Editar")
                        }
                    }
                    
                    // Delete button
                    onDeleteClick?.let { deleteAction ->
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = { deleteAction(bicycle) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
} 