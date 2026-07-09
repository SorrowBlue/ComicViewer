/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil.cache

import dev.zacsweers.metro.Inject
import java.io.File
import okio.Path
import okio.Path.Companion.toOkioPath

@Inject
internal actual class CoilDiskCache {
    actual fun resolve(folder: String): Path =
        File(System.getProperty("java.io.tmpdir"), folder).toOkioPath()
}
