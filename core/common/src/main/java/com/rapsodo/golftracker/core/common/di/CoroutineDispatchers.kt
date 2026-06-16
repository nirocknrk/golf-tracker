package com.rapsodo.golftracker.core.common.di

import javax.inject.Qualifier

/**
 * Hilt qualifier annotations for named [kotlinx.coroutines.CoroutineDispatcher] bindings.
 *
 * Usage:
 *   @Inject @IoDispatcher lateinit var ioDispatcher: CoroutineDispatcher
 *
 * Bindings provided by `DispatcherModule` in `:data`.
 */

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher
