package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ShareFileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import okio.BufferedSource
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
@ShareFileClient
internal actual class ShareFileClient(
    @InjectedParam actual override val bookshelf: ShareContents,
) : FileClient<ShareContents> {
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
