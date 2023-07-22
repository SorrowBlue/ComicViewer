package com.sorrowblue.comicviewer.framework

sealed interface Resource<out D, out E : Resource.ErrorEntity> {

    class Success<D>(val data: D) : Resource<D, Nothing>

    class Error<out E : ErrorEntity>(val error: E) : Resource<Nothing, E>

    /** data class Runtime(reason: RuntimeError) : ErrorEntity */
    interface ErrorEntity
}

inline fun <D, E : Resource.ErrorEntity, R> Resource<D, E>.fold(
    onSuccess: (D) -> R,
    onError: (E) -> R
): R {
    return when (this) {
        is Resource.Error -> onError(error)
        is Resource.Success -> onSuccess(data)
    }
}

inline fun <D, E : Resource.ErrorEntity> Resource<D, E>.onError(onError: (E) -> Unit) {
    if (this is Resource.Error) {
        onError(error)
    }
}
