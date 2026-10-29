/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.common

/**
 * Represents the outcome of an operation that either succeeds with [D] or fails with [E].
 *
 * @param D The type of data returned on success.
 * @param E The type of error returned on failure.
 */
sealed interface Resource<out D, out E> {
    /**
     * Represents a successful outcome containing [data].
     *
     * @param D The type of data.
     * @property data The result data.
     */
    data class Success<out D>(val data: D) : Resource<D, Nothing>

    /**
     * Represents a failed outcome containing [error].
     *
     * @param E The type of error.
     * @property error The error detail.
     */
    data class Error<out E>(val error: E) : Resource<Nothing, E>

    /**
     * Base marker interface for resource errors.
     */
    interface IError

    /**
     * Marker interface for application-level domain errors.
     */
    interface AppError : IError

    /**
     * Represents an unexpected system error wrapping a [throwable].
     *
     * @property throwable The underlying exception or throwable.
     */
    data class SystemError(val throwable: Throwable) : IError
}

/**
 * Transforms a [Resource] into a single value of type [R] by applying [onSuccess] if this is
 * [Resource.Success] or [onError] if this is [Resource.Error].
 */
inline fun <D, E, R> Resource<D, E>.fold(onSuccess: (D) -> R, onError: (E) -> R): R = when (this) {
    is Resource.Error -> onError(error)
    is Resource.Success -> onSuccess(data)
}

/**
 * Invokes [onError] if this is [Resource.Error] and returns the original [Resource].
 */
inline fun <D, E> Resource<D, E>.onError(onError: (E) -> Unit): Resource<D, E> {
    if (this is Resource.Error) {
        onError(error)
    }
    return this
}

/**
 * Invokes [onSuccess] if this is [Resource.Success] and returns the original [Resource].
 */
inline fun <D, E> Resource<D, E>.onSuccess(onSuccess: (D) -> Unit): Resource<D, E> {
    if (this is Resource.Success) {
        onSuccess(data)
    }
    return this
}

/**
 * Returns the encapsulated data if this is [Resource.Success], or `null` if this is [Resource.Error].
 */
fun <D> Resource<D, *>.dataOrNull(): D? {
    if (this is Resource.Success) {
        return data
    }
    return null
}
