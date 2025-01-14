package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.serializer.KOkioSerializer
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

internal actual fun <T : Any> createDataStore(serializer: KOkioSerializer<T>): DataStore<T> {
    val producePath = {
        @OptIn(ExperimentalForeignApi::class)
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        "${requireNotNull(documentDirectory).path}/${serializer.fileName}"
    }
    return createDataStore(
        fileSystem = FileSystem.SYSTEM,
        serializer = serializer,
        producePath = { producePath().toPath() },
    )
}
