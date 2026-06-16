package com.rapsodo.golftracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides [DataStore<Preferences>] as a Hilt singleton.
 *
 * This replaces the previous pattern of creating DataStore via a `Context`
 * extension property inside [ThemeViewModel], which made the ViewModel
 * impossible to unit test (required a real Android Context).
 *
 * Now [ThemeViewModel] declares `DataStore<Preferences>` as a constructor
 * parameter and Hilt injects this singleton automatically.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideThemeDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("theme_prefs") }
        )
}
