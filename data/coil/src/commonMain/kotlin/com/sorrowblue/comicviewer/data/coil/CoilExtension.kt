package com.sorrowblue.comicviewer.data.coil

import coil3.disk.DiskCache
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import kotlin.reflect.KProperty
import okio.BufferedSink
import okio.BufferedSource

internal operator fun <T> Lazy<T>.getValue(receiver: Any?, property: KProperty<*>): T = value

internal fun DiskCache.Editor.abortQuietly() {
    try {
        abort()
    } catch (_: Exception) {
    }
}

internal fun AutoCloseable.closeQuietly() {
    try {
        close()
    } catch (e: RuntimeException) {
        throw e
    } catch (_: Exception) {}
}

expect suspend fun resizeImage(
    source: BufferedSource,
    sink: BufferedSink,
    imageFormat: ImageFormat,
    quality: Int,
)
