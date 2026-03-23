package com.sorrowblue.comicviewer.data.coil.cache

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject
import okio.Path
import okio.Path.Companion.toOkioPath

@Inject
internal actual class CoilDiskCache actual constructor(private val context: PlatformContext) {
    actual fun resolve(folder: String): Path = context.cacheDir
        .resolve(folder)
        .apply { mkdirs() }
        .toOkioPath()
}
