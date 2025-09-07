package com.sorrowblue.comicviewer.data.coil

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@Single
actual class CoilDiskCache actual constructor(context: PlatformContext) {

    actual fun resolve(folder: String): Path {
        @OptIn(ExperimentalForeignApi::class)
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return "${requireNotNull(documentDirectory).path}/$folder".toPath()
    }
}
