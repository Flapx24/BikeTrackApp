package com.example.biketrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biketrack.core.di.DependencyProvider
import com.example.biketrack.presentation.screens.LoginScreen
import com.example.biketrack.presentation.screens.MainScreen
import com.example.biketrack.presentation.screens.RegisterScreen
import com.example.biketrack.presentation.viewmodels.LoginViewModel
import com.example.biketrack.presentation.viewmodels.MainViewModel
import com.example.biketrack.ui.theme.BikeTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DependencyProvider.init(this)
        
        enableEdgeToEdge()
        setContent {
            BikeTrackTheme {
                BikeTrackApp()
            }
        }
    }
}

@Composable
fun BikeTrackApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel { 
                DependencyProvider.provideLoginViewModel() 
            }
            
            LoginScreen(
                loginViewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        
        composable("main") {
            val mainViewModel: MainViewModel = viewModel { 
                DependencyProvider.provideMainViewModel() 
            }
            
            val uiState by remember { derivedStateOf { mainViewModel.uiState } }
            
            // Handle successful logout
            LaunchedEffect(uiState.logoutSuccess) {
                if (uiState.logoutSuccess) {
                    mainViewModel.resetLogoutState()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            }
            
            MainScreen(
                onLogout = { mainViewModel.logout() }
            )
        }
    }
}