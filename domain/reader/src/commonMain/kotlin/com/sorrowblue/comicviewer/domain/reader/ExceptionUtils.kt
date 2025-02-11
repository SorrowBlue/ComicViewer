package com.sorrowblue.comicviewer.domain.reader

internal expect fun Throwable.setCauseIfSupported(cause: Throwable?): Unit

internal expect fun newEOFExceptionWithCause(message: String?, cause: Throwable?): kotlinx.io.EOFException

internal inline fun <T> withOkio2KxIOExceptionMapping(block: () -> T): T {
    try {
        return block()
    } catch (bypassIOE: kotlinx.io.IOException) { // on JVM, kotlinx.io.IOException and okio.IOException are the same
        throw bypassIOE
    } catch (bypassEOF: kotlinx.io.EOFException) { // see above
        throw bypassEOF
    } catch (eofe: okio.EOFException) {
        throw newEOFExceptionWithCause(eofe.message, eofe)
    } catch (ioe: okio.IOException) {
        throw kotlinx.io.IOException(ioe.message, ioe)
    }
}

internal inline fun <T> withKxIO2OkioExceptionMapping(block: () -> T): T {
    try {
        return block()
    } catch (bypassIOE: okio.IOException) {  // on JVM, kotlinx.io.IOException and okio.IOException are the same
        throw bypassIOE
    } catch (bypassEOF: okio.EOFException) { // see above
        throw bypassEOF
    } catch (eofe: kotlinx.io.EOFException) {
        throw okio.EOFException(buildString {
            if (eofe.message != null) {
                append(eofe.message)
            } else {
                append("Intercepted kotlinx.io.EOFException")
            }
            append("\nCaused by: ")
            append(eofe.stackTraceToString())
        }).also { it.setCauseIfSupported(eofe) }
    } catch (ioe: kotlinx.io.IOException) {
        throw okio.IOException(ioe.message, ioe)
    }
}
