package com.sorrowblue.comicviewer.data.remote.client

sealed class FileClientException : RuntimeException() {
    data object NoNetwork : FileClientException()
    data object InvalidAuth : FileClientException()
    data object InvalidServer : FileClientException()
    data object InvalidPath : FileClientException()
}

sealed class FileReaderException : RuntimeException() {
    data object NotSupport : FileReaderException()
}
