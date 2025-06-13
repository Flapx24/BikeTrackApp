package com.example.biketrack.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToRouteDetail: (Long) -> Unit,
    onNavigateToBicycleDetail: (Long) -> Unit,
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
        Dialog(onDismissRequest = { showLogoutDialog = false }) {
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
                        text = "Cerrar Sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "¿Estás seguro de que quieres cerrar sesión?",
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
                            onClick = { showLogoutDialog = false }
                        ) {
                            Text("Cancelar")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
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
                    }
                }
            }
        }
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
                onNavigateToRouteDetail = onNavigateToRouteDetail,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> BicyclesScreen(
                onNavigateToBicycleDetail = onNavigateToBicycleDetail,
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