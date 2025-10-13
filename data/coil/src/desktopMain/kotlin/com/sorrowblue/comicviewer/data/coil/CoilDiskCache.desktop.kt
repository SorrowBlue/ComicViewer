package com.sorrowblue.comicviewer.data.coil

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import java.io.File
import okio.Path
import okio.Path.Companion.toOkioPath
import jakarta.inject.Singleton

@Singleton
actual class CoilDiskCache actual constructor(context: PlatformContext) {
    actual fun resolve(folder: String): Path {
        return File(System.getProperty("java.io.tmpdir"), folder).toOkioPath()
    }
}
