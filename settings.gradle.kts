pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GolfPerformanceTracker"

include(":app")
include(":core:common")
include(":core:ui")
include(":domain")
include(":data")
include(":feature:players")
include(":feature:playerdetail")
include(":feature:shots")
