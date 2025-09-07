package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import kotlin.io.path.createDirectories
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Single

@Single
internal actual class DataStoreMaker actual constructor(private val context: PlatformContext) {

    actual fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T> {
        val datastorePath = context.filesDir.resolve("datastore").also {
            it.createDirectories()
        }
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
