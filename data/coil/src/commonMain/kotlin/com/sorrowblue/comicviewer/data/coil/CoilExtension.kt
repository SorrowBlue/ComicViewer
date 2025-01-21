package com.sorrowblue.comicviewer.data.coil

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import kotlin.reflect.KProperty
import okio.Buffer
import okio.BufferedSink

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

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect suspend fun resizeImage(buffer: Buffer, sink: BufferedSink, imageFormat: ImageFormat, quality: Int)
