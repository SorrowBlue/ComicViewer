package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.serializer.KOkioSerializer
import okio.FileSystem
import okio.Path.Companion.toPath

internal actual fun <T : Any> createDataStore(serializer: KOkioSerializer<T>): DataStore<T> {
    return createDataStore(
        fileSystem = FileSystem.SYSTEM,
        serializer = serializer,
        producePath = { "datastore/${serializer.fileName}".toPath() },
    )
}
