package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactoryMap
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import com.sorrowblue.comicviewer.framework.common.annotation.VisibleForAssistedInject
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.io.Sink
import kotlinx.io.Source

@VisibleForAssistedInject
@AssistedInject
actual class ShareFileClient(
    @Assisted bookshelf: ShareContents,
    fileReaderFactoryMap: FileReaderFactoryMap,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : FileClient<ShareContents>(bookshelf, fileReaderFactoryMap, dispatcher) {
    @ContributesIntoMap(AppScope::class)
    @FileClientKey(ShareContents::class)
    @AssistedFactory
    actual fun interface Factory : FileClient.Factory<ShareContents> {
        actual override fun create(bookshelf: ShareContents): ShareFileClient
    }

    actual override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> {
        TODO("Not yet implemented")
    }

    actual override suspend fun exists(path: String): Boolean {
        TODO("Not yet implemented")
    }

    actual override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        TODO("Not yet implemented")
    }

    actual override suspend fun source(file: File): Source {
        TODO("Not yet implemented")
    }

    actual override suspend fun extractTo(file: File, sink: Sink) {
        TODO("Not yet implemented")
    }

    actual override suspend fun seekableInputStream(file: File): SeekableInputStream {
        TODO("Not yet implemented")
    }

    actual override suspend fun connect(path: String) {
        TODO("Not yet implemented")
    }

    actual override suspend fun attribute(path: String): FileAttribute {
        TODO("Not yet implemented")
    }

    actual override suspend fun fileSize(path: String): Long {
        TODO("Not yet implemented")
    }
}
