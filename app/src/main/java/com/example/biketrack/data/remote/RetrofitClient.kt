package com.example.biketrack.data.remote

import com.example.biketrack.BuildConfig
import com.example.biketrack.core.config.ApiConfig
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS 
                else HttpLoggingInterceptor.Level.NONE
    }
    
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(ApiConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
    
    // Custom LocalDate adapter for Gson
    private class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private val primaryFormatter = DateTimeFormatter.ISO_LOCAL_DATE // This handles YYYY-MM-DD format
        private val alternativeFormatters = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
        )
        
        override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonPrimitive(src?.format(primaryFormatter))
        }
        
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate? {
            return try {
                if (json?.isJsonPrimitive == true && json.asJsonPrimitive.isString) {
                    val dateString = json.asString.trim()
                    
                    // Try primary formatter first
                    try {
                        return LocalDate.parse(dateString, primaryFormatter)
                    } catch (e: Exception) {
                        // Try alternative formatters
                        for (formatter in alternativeFormatters) {
                            try {
                                return LocalDate.parse(dateString, formatter)
                            } catch (e: Exception) {
                                // Continue to next formatter
                            }
                        }
                        // If all formatters fail, log and return null
                        android.util.Log.e("LocalDateAdapter", "Failed to parse date: '$dateString'")
                        return null
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                android.util.Log.e("LocalDateAdapter", "Error deserializing LocalDate: ${e.message}")
                null
            }
        }
    }
    
    // Custom Gson instance with LocalDate adapter
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .setLenient()
        .create()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    

    
    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val routeApiService: RouteApiService = retrofit.create(RouteApiService::class.java)
    val workshopApiService: WorkshopApiService = retrofit.create(WorkshopApiService::class.java)
    val bicycleApiService: BicycleApiService = retrofit.create(BicycleApiService::class.java)
    val componentApiService: ComponentApiService = retrofit.create(ComponentApiService::class.java)
} 