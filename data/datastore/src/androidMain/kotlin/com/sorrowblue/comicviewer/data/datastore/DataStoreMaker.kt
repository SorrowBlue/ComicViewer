package com.sorrowblue.comicviewer.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.dataStoreFile
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.annotation.Singleton

@Singleton
internal actual class DataStoreMaker(private val context: Context) {

    actual fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T> {
        return DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                producePath = { context.applicationContext.dataStoreFile(okioSerializer.fileName).absolutePath.toPath() },
                serializer = okioSerializer
            ),
            corruptionHandler = ReplaceFileCorruptionHandler { okioSerializer.defaultValue },
        )
    }
}
