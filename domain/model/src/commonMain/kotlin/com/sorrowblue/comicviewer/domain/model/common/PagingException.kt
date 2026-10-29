/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.common

/**
 * Base exception for errors occurring during paginated data loading.
 *
 * @param cause The underlying cause of the exception.
 */
sealed class PagingException(cause: Throwable? = null) : RuntimeException(cause) {
    /**
     * Indicates that no network connection is available.
     *
     * @param cause The underlying cause of the exception.
     */
    class NoNetwork(cause: Throwable? = null) : PagingException(cause)

    /**
     * Indicates that authentication failed.
     *
     * @param cause The underlying cause of the exception.
     */
    class InvalidAuth(cause: Throwable? = null) : PagingException(cause)

    /**
     * Indicates that the server address or configuration is invalid.
     *
     * @param cause The underlying cause of the exception.
     */
    class InvalidServer(cause: Throwable? = null) : PagingException(cause)

    /**
     * Indicates that the requested resource was not found.
     *
     * @param cause The underlying cause of the exception.
     */
    class NotFound(cause: Throwable? = null) : PagingException(cause)
}
