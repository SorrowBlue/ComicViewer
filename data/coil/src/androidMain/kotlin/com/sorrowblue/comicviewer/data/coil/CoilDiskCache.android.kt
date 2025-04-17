package com.sorrowblue.comicviewer.data.coil

import android.content.Context
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Singleton

@Singleton
actual class CoilDiskCache(val context: Context) {
    actual fun resolve(folder: String): Path {
        return context.cacheDir.resolve(folder).apply { mkdirs() }.toOkioPath()
    }
}
