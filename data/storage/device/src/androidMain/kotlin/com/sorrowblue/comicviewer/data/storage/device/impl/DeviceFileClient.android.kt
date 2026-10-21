/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.device.impl

import android.content.Context
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileClientKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactoryMap
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.file.extension
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered

internal abstract class BaseAndroidFileClient<T : Bookshelf>(
    bookshelf: T,
    protected val context: Context,
    fileReaderFactoryMap: FileReaderFactoryMap,
    dispatcher: CoroutineDispatcher,
) : FileClient<T>(bookshelf, fileReaderFactoryMap, dispatcher) {

    private val contentResolver = context.contentResolver

    protected val File.uri get() = path.toUri()

    protected abstract fun documentFile(path: String): DocumentFile

    override suspend fun source(file: File): Source = runCommand {
        ParcelFileDescriptor
            .AutoCloseInputStream(
                contentResolver.openFileDescriptor(file.uri, "r"),
            ).asSource().buffered()
    }

    override suspend fun attribute(path: String): FileAttribute = FileAttribute()

    override suspend fun connect(path: String) {
        val exists = runCommand {
            documentFile(path).exists()
        }
        if (!exists) {
            throw FileClientException.InvalidPath()
        }
    }

    override suspend fun exists(path: String): Boolean = runCommand {
        documentFile(path).exists()
    }

    override suspend fun seekableInputStream(file: File): SeekableInputStream = runCommand {
        DeviceSeekableInputStream(context, file.uri)
    }

    protected inline fun <R> runCommand(block: () -> R): R = runCatching(block).getOrElse {
        it.printStackTrace()
        throw when (it) {
            is SecurityException -> FileClientException.InvalidAuth()
            is IllegalArgumentException -> FileClientException.InvalidPath()
            else -> it
        }
    }
}

@AssistedInject
internal class DeviceFileClient(
    @Assisted bookshelf: DeviceStorage,
    context: Context,
    fileReaderFactoryMap: FileReaderFactoryMap,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseAndroidFileClient<DeviceStorage>(bookshelf, context, fileReaderFactoryMap, dispatcher) {
    @ContributesIntoMap(AppScope::class)
    @FileClientKey(DeviceStorage::class)
    @AssistedFactory
    fun interface Factory : FileClient.Factory<DeviceStorage> {
        override fun create(bookshelf: DeviceStorage): DeviceFileClient
    }

    override suspend fun fileSize(path: String): Long = runCatching {
        val file = documentFile(path)
        file.listFiles().sumOf { fileSize(it.uri.toString()) }
    }.getOrElse {
        0
    }

    override suspend fun current(path: String, resolveImageFolder: Boolean): File = runCommand {
        documentFile(path).toFileModel(resolveImageFolder)
    }

    override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> =
        runCommand {
            documentFile(file.path).listFiles().map { it.toFileModel(resolveImageFolder) }
        }

    private fun DocumentFile.toFileModel(resolveImageFolder: Boolean = false): File =
        if (resolveImageFolder && listFiles().any {
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
                sortIndex = 0,
            )
        }

    override fun documentFile(path: String): DocumentFile =
        requireNotNull(DocumentFile.fromTreeUri(context, path.toUri()))
}
