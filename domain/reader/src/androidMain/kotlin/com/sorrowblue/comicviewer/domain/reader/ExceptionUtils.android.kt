package com.sorrowblue.comicviewer.domain.reader

import kotlinx.io.EOFException

internal actual fun Throwable.setCauseIfSupported(cause: Throwable?) {
    initCause(cause)
}

internal actual fun newEOFExceptionWithCause(message: String?, cause: Throwable?): EOFException =
    EOFException(message).also { it.setCauseIfSupported(cause) }
