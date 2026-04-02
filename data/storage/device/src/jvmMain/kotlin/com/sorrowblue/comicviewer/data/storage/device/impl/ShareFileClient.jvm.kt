package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderType
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import okio.BufferedSource

@AssistedInject
internal actual class ShareFileClient(
    @Assisted bookshelf: ShareContents,
    fileReaderFactoryMap: Map<FileReaderType, FileReaderFactory>,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : FileClient<ShareContents>(bookshelf, fileReaderFactoryMap, dispatcher) {
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

    actual override suspend fun bufferedSource(file: File): BufferedSource {
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
