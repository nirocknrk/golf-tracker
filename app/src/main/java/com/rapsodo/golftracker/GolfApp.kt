package com.rapsodo.golftracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application entry point.
 *
 * Annotated with [@HiltAndroidApp] to generate the Hilt component hierarchy.
 * Timber is planted here once; all modules call `Timber.d/e/w` without
 * caring whether a tree is planted — release builds get no-op behaviour.
 */
@HiltAndroidApp
class GolfApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.i("GolfApp started — mock API: ${BuildConfig.USE_MOCK_API}")
    }
}
