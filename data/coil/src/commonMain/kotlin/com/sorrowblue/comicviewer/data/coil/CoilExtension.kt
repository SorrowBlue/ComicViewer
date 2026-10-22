/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import kotlinx.io.Sink
import kotlinx.io.Source

internal fun DiskCache.Editor.abortQuietly() {
    try {
        abort()
    } catch (_: Exception) {
    }
}

@Suppress("TooGenericExceptionCaught")
internal fun AutoCloseable.closeQuietly() {
    try {
        close()
    } catch (e: RuntimeException) {
        throw e
    } catch (_: Exception) {
    }
}

internal expect fun resizeImage(source: Source, sink: Sink, imageFormat: ImageFormat, quality: Int)
