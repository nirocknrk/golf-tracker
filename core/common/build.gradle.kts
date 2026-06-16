// Pure Kotlin JVM library — zero Android dependencies.
// Shared result wrappers, qualifier annotations, and dispatcher abstractions.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin { jvmToolchain(17) }

dependencies {
    // Hilt qualifiers require javax.inject (pure JVM; no Android dep)
    implementation(libs.javax.inject)

    // Coroutines (dispatchers abstraction lives here)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
