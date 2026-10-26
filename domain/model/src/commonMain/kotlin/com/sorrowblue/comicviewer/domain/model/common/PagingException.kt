/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.common

sealed class PagingException(cause: Throwable? = null) : RuntimeException(cause) {
    class NoNetwork(cause: Throwable? = null) : PagingException(cause)

    class InvalidAuth(cause: Throwable? = null) : PagingException(cause)

    class InvalidServer(cause: Throwable? = null) : PagingException(cause)

    class NotFound(cause: Throwable? = null) : PagingException(cause)
}
