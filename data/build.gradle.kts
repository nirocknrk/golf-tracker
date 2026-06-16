// Android library: data layer.
// Room (offline-first DB), Retrofit + Moshi (API), Paging 3 RemoteMediator, Hilt modules.
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace  = "com.rapsodo.golftracker.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        buildConfigField("boolean", "USE_MOCK_API", "true")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

// Room schema export — committed to VCS for migration auditing
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental",   "true")
}

dependencies {
    api(project(":domain"))
    implementation(project(":core:common"))

    // Room (Single Source of Truth)
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Paging 3
    implementation(libs.paging.runtime)

    // Retrofit + Moshi (network layer)
    implementation(libs.bundles.network)
    ksp(libs.moshi.codegen)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // DataStore (theme / user prefs)
    implementation(libs.datastore.preferences)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Logging
    implementation(libs.timber)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}
