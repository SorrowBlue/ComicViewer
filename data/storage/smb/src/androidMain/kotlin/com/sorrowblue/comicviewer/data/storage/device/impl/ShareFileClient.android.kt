package com.sorrowblue.comicviewer.data.storage.device.impl

import android.content.Context
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ShareFileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import okio.BufferedSource
import okio.buffer
import okio.source
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
@ShareFileClient
internal actual class ShareFileClient(
    @InjectedParam override val bookshelf: ShareContents,
    private val context: Context,
) : FileClient<ShareContents> {

    private val contentResolver = context.contentResolver

    override suspend fun bufferedSource(file: File): BufferedSource {
        return kotlin.runCatching {
            ParcelFileDescriptor.AutoCloseInputStream(
                contentResolver.openFileDescriptor(file.uri, "r")
            ).source().buffer()
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth()
                is IllegalArgumentException -> throw FileClientException.InvalidPath()
                else -> throw it
            }
        }
    }

    override suspend fun getAttribute(path: String): FileAttribute? {
        return null
    }

    override suspend fun connect(path: String) {
        kotlin.runCatching {
            documentFile(path).exists()
        }.fold({
            if (!it) {
                throw FileClientException.InvalidPath()
            }
        }) {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth()
                is IllegalArgumentException -> throw FileClientException.InvalidPath()
                else -> throw it
            }
        }
    }

    override suspend fun exists(path: String): Boolean {
        return kotlin.runCatching {
            documentFile(path).exists()
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth()
                is IllegalArgumentException -> throw FileClientException.InvalidPath()
                else -> throw it
            }
        }
    }

    override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        return kotlin.runCatching {
            documentFile(path).toFileModel()
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth()
                is IllegalArgumentException -> throw FileClientException.InvalidPath()
                else -> throw it
            }
        }
    }

    override suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean,
    ): List<File> {
        return kotlin.runCatching {
            file.documentFile.listFiles().map { it.toFileModel() }
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth()
                is IllegalArgumentException -> throw FileClientException.InvalidPath()
                else -> throw it
            }
        }
    }

    override suspend fun seekableInputStream(file: File): SeekableInputStream {
        return kotlin.runCatching {
            DeviceSeekableInputStream(context, file.uri)
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth()
                is IllegalArgumentException -> throw FileClientException.InvalidPath()
                else -> throw it
            }
        }
    }

    private fun DocumentFile.toFileModel(): File {
        return BookFile(
            path = uri.toString(),
            bookshelfId = ShareContents.id,
            name = name?.removeSuffix("/").orEmpty(),
            parent = parentFile?.uri?.toString().orEmpty(),
            size = length(),
            lastModifier = lastModified(),
            isHidden = false,
            sortIndex = 0,
            cacheKey = "",
            totalPageCount = 0,
            lastPageRead = 0,
            lastReadTime = 0
        )
    }

    private val File.uri get() = path.toUri()

    private fun documentFile(path: String): DocumentFile =
        DocumentFile.fromSingleUri(context, path.toUri())!!

    private val File.documentFile: DocumentFile
        get() = DocumentFile.fromSingleUri(
            context,
            uri
        )!!
}
