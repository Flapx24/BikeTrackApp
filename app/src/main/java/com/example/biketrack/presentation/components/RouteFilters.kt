package com.example.biketrack.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteFilters(
    selectedCity: String,
    onCityChange: (String) -> Unit,
    minRating: Double,
    onMinRatingChange: (Double) -> Unit,
    onFilterApply: () -> Unit,
    onFilterClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    // Logic for enabling apply button
    val canApplyFilters = remember(selectedCity, minRating) {
        (selectedCity.isNotBlank() && minRating == 0.0) || 
        (selectedCity.isBlank() && minRating > 0.0) ||
        (selectedCity.isNotBlank() && minRating > 0.0)
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
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
            // Filter toggle button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showFilters = !showFilters },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros de búsqueda",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedCity.isNotBlank() || minRating > 0.0) {
                        TextButton(
                            onClick = { 
                                onFilterClear()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar filtros",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Limpiar")
                        }
                    }
                    
                    Icon(
                        imageVector = if (showFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showFilters) "Contraer filtros" else "Expandir filtros",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            if (showFilters) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // City filter
                OutlinedTextField(
                    value = selectedCity,
                    onValueChange = onCityChange,
                    label = { Text("Ciudad") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar ciudad"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rating filter
                Column {
                    Text(
                        text = if (minRating == 0.0) "Puntuación mínima: Sin valoraciones" 
                               else "Puntuación mínima: ${minRating.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Slider(
                        value = minRating.toFloat(),
                        onValueChange = { onMinRatingChange(it.toDouble()) },
                        valueRange = 0f..5f,
                        steps = 4,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    // Rating labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0★",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (minRating == 0.0) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        repeat(5) { index ->
                            val rating = index + 1
                            Text(
                                text = "$rating★",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (rating <= minRating) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Apply filters button
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onFilterApply()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canApplyFilters,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aplicar filtros",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}