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

internal fun FileClientException.toRemoteException(): RemoteException = when (this) {
    is FileClientException.InvalidAuth -> RemoteException.InvalidAuth()
    is FileClientException.InvalidPath -> RemoteException.NotFound()
    is FileClientException.InvalidServer -> RemoteException.InvalidServer()
    is FileClientException.NoNetwork -> RemoteException.NoNetwork()
}
