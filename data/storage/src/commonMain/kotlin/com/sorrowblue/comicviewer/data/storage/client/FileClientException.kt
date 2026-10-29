/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.domain.model.storage.RemoteException

sealed class FileClientException : RuntimeException() {
    class NoNetwork : FileClientException()

    class InvalidAuth : FileClientException()

    class InvalidServer : FileClientException()

    class InvalidPath : FileClientException()
}

internal fun Throwable.toRemoteException(): RemoteException = when (this) {
    is FileClientException.InvalidAuth -> RemoteException.InvalidAuth(this)
    is FileClientException.InvalidPath -> RemoteException.NotFound(this)
    is FileClientException.InvalidServer -> RemoteException.InvalidServer(this)
    is FileClientException.NoNetwork -> RemoteException.NoNetwork(this)
    is RemoteException -> this
    else -> RemoteException.Unknown(this)
}
