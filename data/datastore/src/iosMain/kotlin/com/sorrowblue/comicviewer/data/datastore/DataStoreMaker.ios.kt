package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@Inject
internal actual class DataStoreMaker actual constructor(
    context: PlatformContext,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
    actual fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T> {
        val producePath = {
            @OptIn(ExperimentalForeignApi::class)
            val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            "${requireNotNull(documentDirectory).path}/${okioSerializer.fileName}"
        }
        return DataStore.Builder(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                producePath = { producePath().toPath() },
                serializer = okioSerializer,
            ),
            context = dispatcher + SupervisorJob(),
        ).setCorruptionHandler(ReplaceFileCorruptionHandler { okioSerializer.defaultValue })
            .build()
    }
}
