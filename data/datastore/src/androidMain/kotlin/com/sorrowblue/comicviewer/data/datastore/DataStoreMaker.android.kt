package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.dataStoreFile
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single

@Single
internal actual class DataStoreMaker actual constructor(private val context: PlatformContext) {

    actual fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T> {
        return DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                producePath = {
                    context.applicationContext.dataStoreFile(
                        okioSerializer.fileName
                    ).absolutePath.toPath()
                },
                serializer = okioSerializer
            ),
            corruptionHandler = ReplaceFileCorruptionHandler { okioSerializer.defaultValue },
        )
    }
}
