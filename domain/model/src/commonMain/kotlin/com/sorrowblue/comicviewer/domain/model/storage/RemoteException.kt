/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.storage

sealed class RemoteException(cause: Throwable? = null) : RuntimeException(cause) {
    class NoNetwork(cause: Throwable? = null) : RemoteException(cause)

    class InvalidAuth(cause: Throwable? = null) : RemoteException(cause)

    class InvalidServer(cause: Throwable? = null) : RemoteException(cause)

    class NotFound(cause: Throwable? = null) : RemoteException(cause)

    class Unknown(cause: Throwable? = null) : RemoteException(cause)
}
