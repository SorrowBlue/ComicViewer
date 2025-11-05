package com.sorrowblue.comicviewer.data.storage.device.impl

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.documentfile.provider.DocumentFile
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import okio.BufferedSource
import okio.buffer
import okio.source

@AssistedInject
internal actual class ShareFileClient(
    @Assisted actual override val bookshelf: ShareContents,
    private val context: Context,
) : FileClient<ShareContents> {

    @AssistedFactory
    actual fun interface Factory : FileClient.Factory<ShareContents> {
        actual override fun create(bookshelf: ShareContents): ShareFileClient
    }

    private val contentResolver = context.contentResolver

    actual override suspend fun bufferedSource(file: File): BufferedSource {
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

    actual override suspend fun attribute(path: String): FileAttribute {
        return FileAttribute()
    }

    actual override suspend fun connect(path: String) {
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

    actual override suspend fun exists(path: String): Boolean {
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

    actual override suspend fun current(path: String, resolveImageFolder: Boolean): File {
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

    actual override suspend fun listFiles(
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

    actual override suspend fun seekableInputStream(file: File): SeekableInputStream {
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

    private val File.uri get() = Uri.parse(path)

    private fun documentFile(path: String): DocumentFile =
        DocumentFile.fromSingleUri(context, Uri.parse(path))!!

    private val File.documentFile: DocumentFile
        get() = DocumentFile.fromSingleUri(
            context,
            uri
        )!!
}
