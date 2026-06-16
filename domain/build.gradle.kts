// Pure Kotlin JVM library — zero Android/Room/Retrofit imports allowed.
// Contains domain models, repository interfaces, and use cases.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin { jvmToolchain(17) }

dependencies {
    // DI qualifiers (javax.inject only — no Hilt Android dep)
    implementation(libs.javax.inject)

    // Coroutines + Flow for use case return types
    implementation(libs.kotlinx.coroutines.core)

    // Paging 3 common (no Android dep) for PagingData in repo interfaces
    implementation(libs.paging.common)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
}
