package com.sorrowblue.comicviewer.data.storage.device.impl

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.documentfile.provider.DocumentFile
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DeviceFileClient
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import okio.BufferedSource
import okio.buffer
import okio.source
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@Factory
@DeviceFileClient
internal actual class DeviceFileClient(
    @InjectedParam actual override val bookshelf: InternalStorage,
    private val context: Context,
) : FileClient<InternalStorage> {

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
            documentFile(path).toFileModel(resolveImageFolder)
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
            file.documentFile.listFiles().map { it.toFileModel(resolveImageFolder) }
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

    private fun DocumentFile.toFileModel(resolveImageFolder: Boolean = false): File {
        return if (resolveImageFolder && listFiles().any {
                it.name.orEmpty().extension in SUPPORTED_IMAGE
            }
        ) {
            BookFolder(
                path = uri.toString(),
                bookshelfId = bookshelf.id,
                name = name?.removeSuffix("/").orEmpty(),
                parent = parentFile?.uri?.toString().orEmpty(),
                size = length(),
                lastModifier = lastModified(),
                isHidden = false,
            )
        } else if (isFile) {
            BookFile(
                path = uri.toString(),
                bookshelfId = bookshelf.id,
                name = name?.removeSuffix("/").orEmpty(),
                parent = parentFile?.uri?.toString().orEmpty(),
                size = length(),
                lastModifier = lastModified(),
                isHidden = false,
            )
        } else {
            Folder(
                path = uri.toString(),
                bookshelfId = bookshelf.id,
                name = name?.removeSuffix("/").orEmpty(),
                parent = parentFile?.uri?.toString().orEmpty(),
                size = length(),
                lastModifier = lastModified(),
                isHidden = false,
                sortIndex = 0
            )
        }
    }

    private val File.uri get() = Uri.parse(path)

    private fun documentFile(path: String): DocumentFile =
        DocumentFile.fromTreeUri(context, Uri.parse(path))!!

    private val File.documentFile: DocumentFile
        get() = DocumentFile.fromTreeUri(
            context,
            uri
        )!!
}
