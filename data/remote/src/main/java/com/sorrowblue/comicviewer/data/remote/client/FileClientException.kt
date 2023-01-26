package com.sorrowblue.comicviewer.data.remote.client

sealed class FileClientException : RuntimeException() {
    object NoNetwork : FileClientException()
    object InvalidAuth : FileClientException()
    object InvalidServer : FileClientException()
    object InvalidPath : FileClientException()
}

sealed class FileReaderException : RuntimeException() {
    object NotSupport : FileReaderException()
}
