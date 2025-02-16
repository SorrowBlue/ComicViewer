package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import com.sorrowblue.comicviewer.framework.common.getAppSpecificConfigDirectory
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Singleton

@Singleton
internal actual class DataStoreMaker {
    actual fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T> {
        val datastorePath = getAppSpecificConfigDirectory("datastore")
        return DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                producePath = { datastorePath.resolve(okioSerializer.fileName).toOkioPath() },
                serializer = okioSerializer,
            ),
            corruptionHandler = ReplaceFileCorruptionHandler { okioSerializer.defaultValue },
        )
    }
}
