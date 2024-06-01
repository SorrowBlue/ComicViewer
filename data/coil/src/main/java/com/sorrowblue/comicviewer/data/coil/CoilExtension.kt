package com.sorrowblue.comicviewer.data.coil

import coil3.disk.DiskCache
import kotlin.reflect.KProperty

internal operator fun <T> dagger.Lazy<T>.getValue(receiver: Any?, property: KProperty<*>): T = get()

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
