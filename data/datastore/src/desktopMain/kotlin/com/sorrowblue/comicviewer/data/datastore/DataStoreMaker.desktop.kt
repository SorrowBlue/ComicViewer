package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import java.io.File
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import org.koin.core.annotation.Singleton

@Singleton
internal actual class DataStoreMaker {
    actual fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T> {
        return DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                producePath = { File(System.getProperty("java.io.tmpdir"), "datastore/${okioSerializer.fileName}").toOkioPath() },
                serializer = okioSerializer,
            ),
            corruptionHandler = ReplaceFileCorruptionHandler { okioSerializer.defaultValue },
        )
    }
}
