package com.example.biketrack.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    val navigationItems = listOf(
        NavigationItem("Rutas", Icons.Default.Home),
        NavigationItem("Bicicletas", Icons.Default.DirectionsBike),
        NavigationItem("Talleres", Icons.Default.Build),
        NavigationItem("Cerrar Sesión", Icons.Default.ExitToApp)
    )
    
    // Logout confirmation modal
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { 
                showLogoutDialog = false
            },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres cerrar sesión?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Cerrar Sesión",
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showLogoutDialog = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BikeTrack",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (index == 3) MaterialTheme.colorScheme.error 
                                      else if (selectedTabIndex == index) MaterialTheme.colorScheme.primary
                                      else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                color = if (index == 3) MaterialTheme.colorScheme.error
                                       else if (selectedTabIndex == index) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        selected = selectedTabIndex == index && index != 3,
                        onClick = {
                            if (index == 3) {
                                // Show logout confirmation modal without changing tab
                                showLogoutDialog = true
                            } else {
                                selectedTabIndex = index
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (index == 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            selectedTextColor = if (index == 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            indicatorColor = if (index == 3) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = if (index == 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = if (index == 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> RoutesScreen(
                modifier = Modifier.padding(innerPadding)
            )
            1 -> BicyclesScreen(
                modifier = Modifier.padding(innerPadding)
            )
            2 -> WorkshopsScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) 