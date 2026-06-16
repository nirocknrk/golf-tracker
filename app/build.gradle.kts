plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace   = "com.rapsodo.golftracker"
    compileSdk  = 36

    defaultConfig {
        applicationId = "com.rapsodo.golftracker"
        minSdk        = 26
        targetSdk     = 36
        versionCode   = 1
        versionName   = "1.0.0"

        // Placeholder base URL — replace with real URL when backend is ready.
        // Override in local.properties: API_BASE_URL=https://api.example.com/v1/
        val apiBaseUrl = project.findProperty("API_BASE_URL") as? String
            ?: "https://api.rapsodo-golf.com/v1/"
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")

        // Toggle mock data (OkHttp interceptor) — true while backend is unavailable
        val useMockApi = project.findProperty("USE_MOCK_API") as? String ?: "true"
        buildConfigField("boolean", "USE_MOCK_API", useMockApi)
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose     = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // Feature modules
    implementation(project(":feature:players"))
    implementation(project(":feature:playerdetail"))
    implementation(project(":feature:shots"))

    // Core
    implementation(project(":core:ui"))
    implementation(project(":core:common"))

    // Data (needed only to wire Hilt root graph)
    implementation(project(":data"))

    // Domain
    implementation(project(":domain"))

    // Compose + Navigation
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // DataStore (theme prefs)
    implementation(libs.datastore.preferences)

    // Logging
    implementation(libs.timber)

    // Debug tooling
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
