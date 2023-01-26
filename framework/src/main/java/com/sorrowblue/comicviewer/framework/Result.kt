package com.sorrowblue.comicviewer.framework

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
        onException: (Exception.Cause) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(error)
        is Exception -> onException(cause)
    }

    inline fun <R> convert(onSuccess: (S) -> R): Result<R, F> = when (this) {
        is Success -> Success(onSuccess(data))
        is Error -> this
        is Exception -> this
    }
    inline fun <R> convertError(onError: (F) -> R): Result<S, R> = when (this) {
        is Success -> this
        is Error -> Error(onError(error))
        is Exception -> this
    }
}

object LowFreeSpace : Result.Exception.Cause
object NoNetwork : Result.Exception.Cause
object IllegalArguments : Result.Exception.Cause
class Unknown(val throws: Throwable) : Result.Exception.Cause
