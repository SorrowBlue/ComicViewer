/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.storage

/**
 * Base exception for errors occurring during remote or storage operations.
 *
 * @param cause The underlying cause of the exception.
 */
sealed class RemoteException(cause: Throwable? = null) : RuntimeException(cause) {
    /**
     * Indicates that no network connection is available.
     *
     * @param cause The underlying cause of the exception.
     */
    class NoNetwork(cause: Throwable? = null) : RemoteException(cause)

    /**
     * Indicates that authentication with the remote server failed.
     *
     * @param cause The underlying cause of the exception.
     */
    class InvalidAuth(cause: Throwable? = null) : RemoteException(cause)

    /**
     * Indicates that the remote server address or configuration is invalid.
     *
     * @param cause The underlying cause of the exception.
     */
    class InvalidServer(cause: Throwable? = null) : RemoteException(cause)

    /**
     * Indicates that the target file or folder was not found on the remote storage.
     *
     * @param cause The underlying cause of the exception.
     */
    class NotFound(cause: Throwable? = null) : RemoteException(cause)

    /**
     * Indicates an unexpected or unclassified remote storage error.
     *
     * @param cause The underlying cause of the exception.
     */
    class Unknown(cause: Throwable? = null) : RemoteException(cause)
}
