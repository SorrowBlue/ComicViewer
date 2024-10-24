package com.sorrowblue.comicviewer.data.storage.client

sealed class FileClientException : RuntimeException() {
    class NoNetwork : FileClientException()
    class InvalidAuth : FileClientException()
    class InvalidServer : FileClientException()
    class InvalidPath : FileClientException()
}

sealed class FileReaderException : RuntimeException() {
    class NotSupport : FileReaderException()
}
