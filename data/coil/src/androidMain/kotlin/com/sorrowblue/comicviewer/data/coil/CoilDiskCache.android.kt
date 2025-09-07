package com.sorrowblue.comicviewer.data.coil

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import okio.Path
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Single

@Single
actual class CoilDiskCache actual constructor(private val context: PlatformContext) {

    actual fun resolve(folder: String): Path {
        return context.cacheDir.resolve(folder).apply { mkdirs() }.toOkioPath()
    }
}
