package com.sorrowblue.comicviewer.domain.model

sealed interface Resource<out D, out E : Resource.IError> {

    class Success<D>(val data: D) : Resource<D, Nothing>

    class Error<out E : IError>(val error: E) : Resource<Nothing, E>

    /** data class Runtime(reason: RuntimeError) : ErrorEntity */
    interface IError

    interface AppError : IError
    class SystemError(val throwable: Throwable) : IError
    data object ReportedSystemError : IError
}

inline fun <D, E : Resource.IError, R> Resource<D, E>.fold(
    onSuccess: (D) -> R,
    onError: (E) -> R,
): R {
    return when (this) {
        is Resource.Error -> onError(error)
        is Resource.Success -> onSuccess(data)
    }
}

inline fun <D, E : Resource.IError> Resource<D, E>.onError(onError: (E) -> Unit): Resource<D, E> {
    if (this is Resource.Error) {
        onError(error)
    }
    return this
}

inline fun <D, E : Resource.IError> Resource<D, E>.onSuccess(onSuccess: (D) -> Unit): Resource<D, E> {
    if (this is Resource.Success) {
        onSuccess(data)
    }
    return this
}

fun <D> Resource<D, *>.dataOrNull(): D? {
    if (this is Resource.Success) {
        return data
    }
    return null
}
