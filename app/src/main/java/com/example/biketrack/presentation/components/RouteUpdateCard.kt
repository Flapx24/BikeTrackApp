package com.example.biketrack.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.biketrack.domain.entities.RouteUpdate
import com.example.biketrack.domain.entities.UpdateType
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun RouteUpdateCard(
    routeUpdate: RouteUpdate,
    isCurrentUserUpdate: Boolean = false,
    onEditClick: ((RouteUpdate) -> Unit)? = null,
    onDeleteClick: ((RouteUpdate) -> Unit)? = null,
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
                .padding(16.dp)
        ) {
            // Header with type indicator and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Update type chip
                UpdateTypeChip(
                    type = routeUpdate.type,
                    resolved = routeUpdate.resolved,
                    modifier = Modifier.weight(1f)
                )
                
                // Action buttons for current user's update
                if (isCurrentUserUpdate && (onEditClick != null || onDeleteClick != null)) {
                    Row {
                        onEditClick?.let { editAction ->
                            IconButton(
                                onClick = { editAction(routeUpdate) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar actualización",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        onDeleteClick?.let { deleteAction ->
                            IconButton(
                                onClick = { deleteAction(routeUpdate) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar actualización",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Description
            Text(
                text = routeUpdate.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Fecha",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = routeUpdate.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UpdateTypeChip(
    type: UpdateType,
    resolved: Boolean,
    modifier: Modifier = Modifier
) {
    val (baseText, icon, baseColor) = when (type) {
        UpdateType.INCIDENT -> Triple("Incidente", Icons.Default.Warning, Color(0xFFFF8A65))
        UpdateType.INFO -> Triple("Información", Icons.Default.Info, Color(0xFF2196F3))
        UpdateType.MAINTENANCE -> Triple("Mantenimiento", Icons.Default.Build, Color(0xFFFFB300))
        UpdateType.CLOSURE -> Triple("Cierre", Icons.Default.Block, MaterialTheme.colorScheme.error)
        UpdateType.OTHER -> Triple("Otro", Icons.Default.NotificationsActive, Color(0xFF26A69A))
    }
    
    // Determinar texto y color basado en el estado de resolución
    val (text, color) = if (resolved) {
        "$baseText - Resuelto" to MaterialTheme.colorScheme.primary
    } else {
        baseText to baseColor
    }
    
    AssistChip(
        onClick = { },
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
                if (resolved) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Resuelto",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = type.name,
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