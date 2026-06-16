package com.rapsodo.golftracker.core.common.util

/**
 * Generic wrapper for async data states surfaced from repository → ViewModel.
 * Use [Loading] immediately, replace with [Success] or [Error] on completion.
 */
sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Resource<Nothing>()
}

/** Convenience: true only for [Resource.Loading]. */
val Resource<*>.isLoading get() = this is Resource.Loading

/** Unwraps [Resource.Success.data] or returns null. */
fun <T> Resource<T>.dataOrNull(): T? = (this as? Resource.Success)?.data
