package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import dev.zacsweers.metro.Inject
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.resolve
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

@Inject
internal class DataStoreMaker(@IoDispatcher private val dispatcher: CoroutineDispatcher) {
    fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T> = DataStore.Builder(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            producePath = {
                FileKit.filesDir
                    .resolve("datastore")
                    .resolve(okioSerializer.fileName)
                    .path.toPath()
            },
            serializer = okioSerializer,
        ),
        context = dispatcher + SupervisorJob(),
    ).setCorruptionHandler(ReplaceFileCorruptionHandler { okioSerializer.defaultValue })
        .build()
}
