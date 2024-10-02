package com.sorrowblue.comicviewer.domain.model

sealed class Result<out S, out F> {

    val dataOrNull get() = if (this is Success) data else null
    val errorOrNull get() = if (this is Error) error else null
    val causeOrNull get() = if (this is Exception) cause else null

    class Success<out S>(val data: S) : Result<S, Nothing>()

    class Error<out F>(val error: F) : Result<Nothing, F>()

    class Exception(val cause: Cause) : Result<Nothing, Nothing>() {
        interface Cause
    }

    inline fun <R> fold(
        onSuccess: (S) -> R,
        onError: (F) -> R,
        onException: (Exception.Cause) -> R,
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(error)
        is Exception -> onException(cause)
    }
}
