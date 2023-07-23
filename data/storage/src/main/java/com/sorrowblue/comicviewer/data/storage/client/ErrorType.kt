package com.sorrowblue.comicviewer.data.storage.client

sealed interface ErrorType {

    sealed interface Client : ErrorType {

        data object Network : Client

        data object ServiceUnavailable : Client

        data object NotFound : Client

        data object Server : Client
        data object InvalidPath : Client
        data object ACCESS_DENIED : Client

    }

    object Unknown : ErrorType

    // other categories of Error
}

sealed class Resource<T> {
    data class Success<T>(val data: T): Resource<T>()
    data class Error<T>(val error: ErrorType): Resource<T>()
}
