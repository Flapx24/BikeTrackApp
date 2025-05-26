package com.example.biketrack.core.di

import android.content.Context
import com.example.biketrack.data.local.SecureStorageManager
import com.example.biketrack.data.remote.RetrofitClient
import com.example.biketrack.data.repositories.AuthRepositoryImpl
import com.example.biketrack.domain.repositories.AuthRepository
import com.example.biketrack.domain.usecases.auth.AutoLoginUseCase
import com.example.biketrack.domain.usecases.auth.LoginUseCase
import com.example.biketrack.domain.usecases.auth.LogoutUseCase
import com.example.biketrack.presentation.viewmodels.LoginViewModel
import com.example.biketrack.presentation.viewmodels.MainViewModel

object DependencyProvider {
    
    private lateinit var context: Context
    
    fun init(context: Context) {
        this.context = context
    }
    
    // Storage
    private val secureStorageManager by lazy {
        SecureStorageManager(context)
    }
    
    // API Services
    private val authApiService by lazy {
        RetrofitClient.authApiService
    }
    
    // Repositories
    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiService, secureStorageManager)
    }
    
    // Use Cases
    private val loginUseCase by lazy {
        LoginUseCase(authRepository)
    }
    
    private val autoLoginUseCase by lazy {
        AutoLoginUseCase(authRepository)
    }
    
    private val logoutUseCase by lazy {
        LogoutUseCase(authRepository)
    }
    
    // ViewModels
    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(loginUseCase, autoLoginUseCase)
    }
    
    fun provideMainViewModel(): MainViewModel {
        return MainViewModel(logoutUseCase)
    }
} 