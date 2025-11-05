package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import okio.BufferedSource

internal expect class ShareFileClient : FileClient<ShareContents> {

    fun interface Factory : FileClient.Factory<ShareContents> {
        override fun create(bookshelf: ShareContents): ShareFileClient
    }

    override val bookshelf: ShareContents

    override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File>

    override suspend fun exists(path: String): Boolean

    override suspend fun current(path: String, resolveImageFolder: Boolean): File

    override suspend fun bufferedSource(file: File): BufferedSource

    override suspend fun seekableInputStream(file: File): SeekableInputStream

    override suspend fun connect(path: String)

    override suspend fun attribute(path: String): FileAttribute
}
