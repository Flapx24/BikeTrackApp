package com.example.biketrack.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.biketrack.domain.entities.BicycleSummary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBicycleDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, totalKilometers: Double, lastMaintenanceDate: LocalDate?) -> Unit,
    isLoading: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var totalKilometers by remember { mutableStateOf("0") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Validate that date is not in the future
    val isDateValid = selectedDate?.let { it <= LocalDate.now() } ?: true
    
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
                    text = "Nueva Bicicleta",
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
                    placeholder = { Text("Ej: Mi bicicleta de montaña") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Total kilometers field
                OutlinedTextField(
                    value = totalKilometers,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null) {
                            totalKilometers = value
                        }
                    },
                    label = { Text("Kilómetros totales") },
                    placeholder = { Text("0") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("km") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Last maintenance date field
                OutlinedTextField(
                    value = selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                    onValueChange = { },
                    label = { Text("Último mantenimiento (opcional)") },
                    placeholder = { Text("Seleccionar fecha") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    enabled = !isLoading,
                    isError = !isDateValid,
                    supportingText = if (!isDateValid) {
                        { Text(
                            "La fecha no puede ser posterior a hoy",
                            color = MaterialTheme.colorScheme.error
                        ) }
                    } else null,
                    trailingIcon = {
                        Row {
                            if (selectedDate != null) {
                                IconButton(
                                    onClick = { selectedDate = null }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Limpiar fecha"
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showDatePicker = true }
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = "Seleccionar fecha"
                                )
                            }
                        }
                    }
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
                            val kilometers = totalKilometers.toDoubleOrNull() ?: 0.0
                            onConfirm(name.trim(), kilometers, selectedDate)
                        },
                        enabled = name.isNotBlank() && isDateValid && !isLoading
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
    
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedTimestamp ->
                selectedTimestamp?.let {
                    selectedDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBicycleDialog(
    bicycle: BicycleSummary,
    onDismiss: () -> Unit,
    onConfirm: (bicycleId: Long, name: String, totalKilometers: Double, lastMaintenanceDate: LocalDate?) -> Unit,
    isLoading: Boolean = false
) {
    var name by remember { mutableStateOf(bicycle.name) }
    var totalKilometers by remember { mutableStateOf(bicycle.totalKilometers.toString()) }
    var selectedDate by remember { mutableStateOf(bicycle.lastMaintenanceDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Validate that date is not in the future
    val isDateValid = selectedDate?.let { it <= LocalDate.now() } ?: true
    
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
                    text = "Editar Bicicleta",
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
                    placeholder = { Text("Ej: Mi bicicleta de montaña") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Total kilometers field
                OutlinedTextField(
                    value = totalKilometers,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null) {
                            totalKilometers = value
                        }
                    },
                    label = { Text("Kilómetros totales") },
                    placeholder = { Text("0") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("km") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Last maintenance date field
                OutlinedTextField(
                    value = selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                    onValueChange = { },
                    label = { Text("Último mantenimiento (opcional)") },
                    placeholder = { Text("Seleccionar fecha") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    enabled = !isLoading,
                    isError = !isDateValid,
                    supportingText = if (!isDateValid) {
                        { Text(
                            "La fecha no puede ser posterior a hoy",
                            color = MaterialTheme.colorScheme.error
                        ) }
                    } else null,
                    trailingIcon = {
                        Row {
                            if (selectedDate != null) {
                                IconButton(
                                    onClick = { selectedDate = null }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Limpiar fecha"
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showDatePicker = true }
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = "Seleccionar fecha"
                                )
                            }
                        }
                    }
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
                            val kilometers = totalKilometers.toDoubleOrNull() ?: 0.0
                            onConfirm(bicycle.id, name.trim(), kilometers, selectedDate)
                        },
                        enabled = name.isNotBlank() && isDateValid && !isLoading
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
    
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedTimestamp ->
                selectedTimestamp?.let {
                    selectedDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun DeleteBicycleDialog(
    bicycle: BicycleSummary,
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
                    text = "Eliminar Bicicleta",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "¿Estás seguro de que quieres eliminar la bicicleta \"${bicycle.name}\"?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Esta acción no se puede deshacer y eliminará todos los componentes asociados.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Only allow dates up to today
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
} 