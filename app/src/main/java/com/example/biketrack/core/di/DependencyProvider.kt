package com.example.biketrack.core.di

import android.content.Context
import com.example.biketrack.data.local.SecureStorageManager
import com.example.biketrack.data.remote.RetrofitClient
import com.example.biketrack.data.repositories.AuthRepositoryImpl
import com.example.biketrack.data.repositories.RouteRepositoryImpl
import com.example.biketrack.data.repositories.WorkshopRepositoryImpl
import com.example.biketrack.domain.repositories.AuthRepository
import com.example.biketrack.domain.repositories.RouteRepository
import com.example.biketrack.domain.repositories.WorkshopRepository
import com.example.biketrack.domain.usecases.auth.AutoLoginUseCase
import com.example.biketrack.domain.usecases.auth.LoginUseCase
import com.example.biketrack.domain.usecases.auth.LogoutUseCase
import com.example.biketrack.domain.usecases.auth.RegisterUseCase
import com.example.biketrack.domain.usecases.route.FilterRoutesUseCase
import com.example.biketrack.domain.usecases.route.GetRoutesUseCase
import com.example.biketrack.domain.usecases.workshop.GetWorkshopByIdUseCase
import com.example.biketrack.domain.usecases.workshop.GetWorkshopsByCityUseCase
import com.example.biketrack.presentation.viewmodels.LoginViewModel
import com.example.biketrack.presentation.viewmodels.MainViewModel
import com.example.biketrack.presentation.viewmodels.RegisterViewModel
import com.example.biketrack.presentation.viewmodels.RoutesViewModel
import com.example.biketrack.presentation.viewmodels.WorkshopsViewModel

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
    
    private val routeApiService by lazy {
        RetrofitClient.routeApiService
    }
    
    private val workshopApiService by lazy {
        RetrofitClient.workshopApiService
    }
    
    // Repositories
    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApiService, secureStorageManager)
    }
    
    private val routeRepository: RouteRepository by lazy {
        RouteRepositoryImpl(routeApiService)
    }
    
    private val workshopRepository: WorkshopRepository by lazy {
        WorkshopRepositoryImpl(workshopApiService)
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
    
    private val registerUseCase by lazy {
        RegisterUseCase(authRepository)
    }
    
    private val getRoutesUseCase by lazy {
        GetRoutesUseCase(routeRepository)
    }
    
    private val filterRoutesUseCase by lazy {
        FilterRoutesUseCase(routeRepository)
    }
    
    private val getWorkshopsByCityUseCase by lazy {
        GetWorkshopsByCityUseCase(workshopRepository)
    }
    
    private val getWorkshopByIdUseCase by lazy {
        GetWorkshopByIdUseCase(workshopRepository)
    }
    
    // ViewModels
    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(loginUseCase, autoLoginUseCase)
    }
    
    fun provideMainViewModel(): MainViewModel {
        return MainViewModel(logoutUseCase)
    }
    
    fun provideRegisterViewModel(): RegisterViewModel {
        return RegisterViewModel(registerUseCase)
    }
    
    fun provideRoutesViewModel(): RoutesViewModel {
        return RoutesViewModel(getRoutesUseCase, filterRoutesUseCase)
    }
    
    fun provideWorkshopsViewModel(): WorkshopsViewModel {
        return WorkshopsViewModel(getWorkshopsByCityUseCase, getWorkshopByIdUseCase)
    }
} 