package com.sorrowblue.comicviewer.data.coil

import okio.Path
import okio.Path.Companion.toPath

actual class CoilDiskCache {
    actual fun resolve(folder: String): Path {
        return folder.toPath()
    }
}
