package com.sorrowblue.comicviewer.data.coil

import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import kotlin.reflect.KProperty

internal operator fun <T> dagger.Lazy<T>.getValue(receiver: Any?, property: KProperty<*>): T = get()

@OptIn(ExperimentalCoilApi::class)
internal fun DiskCache.Editor.abortQuietly() {
    try {
        abort()
    } catch (_: Exception) {
    }
}
