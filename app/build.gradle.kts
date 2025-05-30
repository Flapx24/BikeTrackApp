import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

/**
 * Function to get configuration properties from environment variables or local.properties
 * Priority: Environment Variables > local.properties
 * 
 * Environment variable naming: 
 * - Property "biketrack.api.base_url" becomes "BIKETRACK_API_BASE_URL"
 * - Dots (.) are replaced with underscores (_) and converted to uppercase
 * 
 * Throws a descriptive error if the property is not found in either location
 */
fun getConfigProperty(propertyName: String): String {
    // First check environment variables (dots replaced with underscores, uppercase)
    val envVarName = propertyName.replace(".", "_").uppercase()
    val envValue = System.getenv(envVarName)
    if (!envValue.isNullOrBlank()) {
        return envValue
    }

    // Then check local.properties
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        val localProperties = Properties()
        localProperties.load(localPropertiesFile.inputStream())
        val localValue = localProperties.getProperty(propertyName)
        if (!localValue.isNullOrBlank()) {
            return localValue
        }
    }

    // If not found, throw a simple and direct error
    throw GradleException("""
        Missing required configuration: '$propertyName'
        
        Set one of these:
        
        Environment variable: $envVarName
        OR
        In local.properties: $propertyName=your_value
    """.trimIndent())
}

android {
    namespace = "com.example.biketrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.biketrack"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Read API configuration from properties or environment variables
        val requiredProperties = listOf("biketrack.api.base_url", "biketrack.api.timeout_seconds")
        val missingProperties = mutableListOf<String>()
        val configValues = mutableMapOf<String, String>()
        
        // Check all required properties first
        for (property in requiredProperties) {
            try {
                configValues[property] = getConfigProperty(property)
            } catch (e: Exception) {
                missingProperties.add(property)
            }
        }
        
        // If any are missing, show them all at once
        if (missingProperties.isNotEmpty()) {
                         val errorMessage = buildString {
                 appendLine("Missing required configuration properties:")
                 appendLine()
                 for (property in missingProperties) {
                     val envVar = property.replace(".", "_").uppercase()
                     appendLine("- $property")
                     appendLine("  Environment variable: $envVar")
                     appendLine("  OR in local.properties: $property=your_value")
                     appendLine()
                 }
             }
            throw GradleException(errorMessage.trimEnd())
        }
        
        // All properties are available, set them
        buildConfigField("String", "API_BASE_URL", "\"${configValues["biketrack.api.base_url"]}\"")
        buildConfigField("long", "API_TIMEOUT_SECONDS", "${configValues["biketrack.api.timeout_seconds"]}L")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto)
    implementation(libs.gson)
    implementation(libs.coil.compose)
    implementation("org.osmdroid:osmdroid-android:6.1.17")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}