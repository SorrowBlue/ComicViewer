package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.SmbFileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import okio.BufferedSource
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
@SmbFileClient
internal actual class SmbFileClient(
    @InjectedParam override val bookshelf: Bookshelf,
) : FileClient {
    override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> {
        TODO("Not yet implemented")
    }

    override suspend fun exists(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        TODO("Not yet implemented")
    }

    override suspend fun bufferedSource(file: File): BufferedSource {
        TODO("Not yet implemented")
    }

    override suspend fun seekableInputStream(file: File): SeekableInputStream {
        TODO("Not yet implemented")
    }

    override suspend fun connect(path: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getAttribute(path: String): FileAttribute? {
        TODO("Not yet implemented")
    }

}
