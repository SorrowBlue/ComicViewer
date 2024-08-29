package com.sorrowblue.comicviewer.domain.model

sealed interface Resource<out D, out E> {

    class Success<D>(val data: D) : Resource<D, Nothing>

    class Error<out E>(val error: E) : Resource<Nothing, E>

    /** data class Runtime(reason: RuntimeError) : ErrorEntity */
    interface IError

    interface AppError : IError
    class SystemError(val throwable: Throwable) : IError
    data object ReportedSystemError : IError
}

val Resource<*, *>.isError
    get() = when (this) {
        is Resource.Error -> true
        is Resource.Success -> false
    }

val Resource<*, *>.isSuccess
    get() = when (this) {
        is Resource.Error -> false
        is Resource.Success -> true
    }

inline fun <D, E, R> Resource<D, E>.fold(
    onSuccess: (D) -> R,
    onError: (E) -> R,
): R {
    return when (this) {
        is Resource.Error -> onError(error)
        is Resource.Success -> onSuccess(data)
    }
}

inline fun <D, E> Resource<D, E>.onError(onError: (E) -> Unit): Resource<D, E> {
    if (this is Resource.Error) {
        onError(error)
    }
    return this
}

inline fun <D> Resource<D, Unit>.onSuccess(onSuccess: (D) -> Unit): Resource<D, Unit> {
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
