package com.sorrowblue.comicviewer.data.coil

import android.content.Context
import coil3.PlatformContext
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Singleton

@Singleton
actual class CoilDiskCache(val context: Context) {
    actual fun resolve(folder: String): Path {
        return context.cacheDir.resolve(folder).apply { mkdirs() }.toOkioPath()
    }
}
//
//@Singleton
//fun providePlatformContext(context: Context): PlatformContext = context
