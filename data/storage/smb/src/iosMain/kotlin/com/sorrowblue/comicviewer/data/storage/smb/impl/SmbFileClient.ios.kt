package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import okio.BufferedSource

@AssistedInject
internal actual class SmbFileClient(@Assisted actual override val bookshelf: SmbServer) :
    FileClient<SmbServer> {

    @AssistedFactory
    actual interface Factory : FileClient.Factory<SmbServer> {
        actual override fun create(bookshelf: SmbServer): SmbFileClient
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
}
