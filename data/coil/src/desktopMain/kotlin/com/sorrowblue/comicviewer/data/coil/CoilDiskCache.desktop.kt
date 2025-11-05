package com.sorrowblue.comicviewer.data.coil

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject
import java.io.File
import okio.Path
import okio.Path.Companion.toOkioPath

@Inject
actual class CoilDiskCache actual constructor(context: PlatformContext) {

    actual fun resolve(folder: String): Path {
        return File(System.getProperty("java.io.tmpdir"), folder).toOkioPath()
    }
}
