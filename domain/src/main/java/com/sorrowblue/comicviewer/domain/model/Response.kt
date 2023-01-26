package com.sorrowblue.comicviewer.domain.model

sealed class Response<out T> {
    class Success<out T>(val data: T) : Response<T>()

    class Error(val exception: Throwable) : Response<Nothing>()

    inline fun <R> fold(onSuccess: (T) -> R, onError: (Throwable) -> R): R {
        return when (this) {
            is Error -> onError(this.exception)
            is Success -> onSuccess(this.data)
        }
    }

    inline fun onSuccess(body: (T) -> Unit): Response<T> {
        if (this is Success) {
            body.invoke(data)
        }
        return this
    }

    inline fun onError(body: (Throwable) -> Unit): Response<T> {
        if (this is Error) {
            body.invoke(exception)
        }
        return this
    }

}
