package com.sorrowblue.comicviewer.data.coil

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import kotlin.reflect.KProperty
import kotlinx.io.Sink
import kotlinx.io.Source

internal operator fun <T> Lazy<T>.getValue(receiver: Any?, property: KProperty<*>): T = value

internal fun DiskCache.Editor.abortQuietly() {
    try {
        abort()
    } catch (_: Exception) {
    }
}

fun AutoCloseable.closeQuietly() {
    kotlin.runCatching {
        close()
    }
}

expect suspend fun resizeImage(source: Source, sink: Sink, imageFormat: ImageFormat, quality: Int)
