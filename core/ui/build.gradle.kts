// Android library providing the shared design system:
// Material 3 theme, typography, shapes, colors, reusable Composables, and charts.
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace  = "com.rapsodo.golftracker.core.ui"
    compileSdk = 36

    defaultConfig { minSdk = 26 }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // Compose BOM — version-locks all compose-* artifacts
    api(platform(libs.compose.bom))
    api(libs.bundles.compose.ui)
    api(libs.paging.compose)
    api(libs.coil.compose)
    api(libs.coil.okhttp)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.datastore.preferences)

    implementation(project(":domain"))

    debugImplementation(libs.compose.ui.tooling)
}
