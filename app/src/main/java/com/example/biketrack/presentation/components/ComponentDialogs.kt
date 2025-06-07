package com.example.biketrack.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.biketrack.domain.entities.BicycleComponent

@Composable
fun CreateComponentDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, maxKilometers: Double, currentKilometers: Double) -> Unit,
    isLoading: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var maxKilometers by remember { mutableStateOf("") }
    var currentKilometers by remember { mutableStateOf("0") }
    
    Dialog(onDismissRequest = onDismiss) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Nuevo Componente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre *") },
                    placeholder = { Text("Ej: Cadena, Frenos, Ruedas...") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Max kilometers field
                OutlinedTextField(
                    value = maxKilometers,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null) {
                            maxKilometers = value
                        }
                    },
                    label = { Text("Kilómetros máximos *") },
                    placeholder = { Text("1000") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("km") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Current kilometers field
                OutlinedTextField(
                    value = currentKilometers,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null) {
                            currentKilometers = value
                        }
                    },
                    label = { Text("Kilómetros actuales") },
                    placeholder = { Text("0") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("km") }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            val maxKm = maxKilometers.toDoubleOrNull() ?: 0.0
                            val currentKm = currentKilometers.toDoubleOrNull() ?: 0.0
                            onConfirm(name.trim(), maxKm, currentKm)
                        },
                        enabled = name.isNotBlank() && maxKilometers.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Crear")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditComponentDialog(
    component: BicycleComponent,
    onDismiss: () -> Unit,
    onConfirm: (name: String, maxKilometers: Double, currentKilometers: Double) -> Unit,
    isLoading: Boolean = false
) {
    var name by remember { mutableStateOf(component.name) }
    var maxKilometers by remember { mutableStateOf(component.maxKilometers.toString()) }
    var currentKilometers by remember { mutableStateOf(component.currentKilometers.toString()) }
    
    Dialog(onDismissRequest = onDismiss) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Editar Componente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre *") },
                    placeholder = { Text("Ej: Cadena, Frenos, Ruedas...") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Max kilometers field
                OutlinedTextField(
                    value = maxKilometers,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null) {
                            maxKilometers = value
                        }
                    },
                    label = { Text("Kilómetros máximos *") },
                    placeholder = { Text("1000") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("km") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Current kilometers field
                OutlinedTextField(
                    value = currentKilometers,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null) {
                            currentKilometers = value
                        }
                    },
                    label = { Text("Kilómetros actuales") },
                    placeholder = { Text("0") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("km") }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            val maxKm = maxKilometers.toDoubleOrNull() ?: 0.0
                            val currentKm = currentKilometers.toDoubleOrNull() ?: 0.0
                            onConfirm(name.trim(), maxKm, currentKm)
                        },
                        enabled = name.isNotBlank() && maxKilometers.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteComponentDialog(
    component: BicycleComponent,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean = false
) {
    Dialog(onDismissRequest = onDismiss) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Eliminar Componente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "¿Estás seguro de que quieres eliminar el componente \"${component.name}\"? Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = onConfirm,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onError,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Eliminar",
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddKilometersDialog(
    onDismiss: () -> Unit,
    onConfirm: (kilometers: Double) -> Unit,
    isLoading: Boolean = false
) {
    var kilometers by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Añadir Kilómetros",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Los kilómetros se añadirán tanto a la bicicleta como a todos sus componentes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = kilometers,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null) {
                            kilometers = value
                        }
                    },
                    label = { Text("Kilómetros *") },
                    placeholder = { Text("10.5") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("km") }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            val km = kilometers.toDoubleOrNull() ?: 0.0
                            if (km > 0) onConfirm(km)
                        },
                        enabled = kilometers.isNotBlank() && (kilometers.toDoubleOrNull() ?: 0.0) > 0 && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Añadir")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubtractKilometersDialog(
    onDismiss: () -> Unit,
    onConfirm: (kilometers: Double) -> Unit,
    isLoading: Boolean = false
) {
    var kilometers by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Restar Kilómetros",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Los kilómetros se restarán tanto de la bicicleta como de todos sus componentes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = kilometers,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null) {
                            kilometers = value
                        }
                    },
                    label = { Text("Kilómetros *") },
                    placeholder = { Text("10.5") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("km") }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            val km = kilometers.toDoubleOrNull() ?: 0.0
                            if (km > 0) onConfirm(km)
                        },
                        enabled = kilometers.isNotBlank() && (kilometers.toDoubleOrNull() ?: 0.0) > 0 && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onError,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Restar",
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
            }
        }
    }
} 