package com.sorrowblue.comicviewer.data.coil

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject
import okio.Path
import okio.Path.Companion.toOkioPath

@Inject
actual class CoilDiskCache actual constructor(private val context: PlatformContext) {
    actual fun resolve(folder: String): Path = context.cacheDir
        .resolve(folder)
        .apply { mkdirs() }
        .toOkioPath()
}
