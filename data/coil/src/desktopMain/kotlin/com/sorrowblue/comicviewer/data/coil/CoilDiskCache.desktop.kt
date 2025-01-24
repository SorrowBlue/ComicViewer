package com.sorrowblue.comicviewer.data.coil

import java.io.File
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Singleton

@Singleton
actual class CoilDiskCache {
    actual fun resolve(folder: String): Path {
        return File(System.getProperty("java.io.tmpdir"), folder).toOkioPath()
    }
}
