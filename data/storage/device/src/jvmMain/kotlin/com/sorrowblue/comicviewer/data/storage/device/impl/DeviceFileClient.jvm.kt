/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.device.impl

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
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal abstract class BaseJvmFileClient<T : Bookshelf>(
    bookshelf: T,
    fileReaderFactoryMap: FileReaderFactoryMap,
    dispatcher: CoroutineDispatcher,
) : FileClient<T>(bookshelf, fileReaderFactoryMap, dispatcher) {

    override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> {
        logcat { "listFiles(file.path=${file.path}, resolveImageFolder=$resolveImageFolder)" }
        return kotlin.runCatching {
            Files.list(Path(file.path)).use { stream ->
                stream.map { it.toFileModel(resolveImageFolder) }.toList()
            }
        }.onFailure {
            logcat(LogPriority.ERROR) { it.asLog() }
            throw when (it) {
                is SecurityException -> FileClientException.InvalidAuth()
                is IllegalArgumentException -> FileClientException.InvalidPath()
                else -> it
            }
        }.getOrThrow()
    }

    override suspend fun exists(path: String): Boolean {
        logcat { "exists(path=$path)" }
        return Path(path).exists()
    }

    override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        logcat { "current(path=$path, resolveImageFolder=$resolveImageFolder)" }
        return Path(path).toFileModel(resolveImageFolder)
    }

    override suspend fun source(file: File): Source =
        SystemFileSystem.source(kotlinx.io.files.Path(file.path)).buffered()

    override suspend fun seekableInputStream(file: File): SeekableInputStream =
        LocalFileSeekableInputStream(Path(file.path))

    override suspend fun connect(path: String) {
        logcat { "connect(path=$path)" }
        kotlin.runCatching {
            Path(path).exists()
        }.fold({
            if (!it) {
                throw FileClientException.InvalidPath()
            }
        }) {
            logcat(LogPriority.ERROR) { it.asLog() }
            throw when (it) {
                is SecurityException -> FileClientException.InvalidAuth()
                is IllegalArgumentException -> FileClientException.InvalidPath()
                else -> it
            }
        }
    }

    override suspend fun attribute(path: String): FileAttribute = FileAttribute()

    override suspend fun fileSize(path: String): Long {
        val ioPath = Path(path)
        return if (ioPath.isDirectory()) {
            withContext(dispatcher) {
                Files.list(ioPath).use { it.toList() }
            }.sumOf { fileSize(it.absolutePathString()) }
        } else {
            ioPath.fileSize()
        }
    }

    private fun Path.toFileModel(resolveImageFolder: Boolean = false): File =
        if (resolveImageFolder && isDirectory() && Files.list(this).use { stream ->
                stream.anyMatch { it.name.extension in SUPPORTED_IMAGE }
            }
        ) {
            BookFolder(
                path = absolutePathString(),
                bookshelfId = bookshelf.id,
                name = name.removeSuffix("\\"),
                parent = parent?.absolutePathString().orEmpty(),
                size = fileSize(),
                lastModifier = getLastModifiedTime().toMillis(),
                isHidden = false,
            )
        } else if (!isDirectory()) {
            BookFile(
                path = absolutePathString(),
                bookshelfId = bookshelf.id,
                name = name.removeSuffix("\\"),
                parent = parent?.absolutePathString().orEmpty(),
                size = fileSize(),
                lastModifier = getLastModifiedTime().toMillis(),
                isHidden = false,
            )
        } else {
            Folder(
                path = absolutePathString(),
                bookshelfId = bookshelf.id,
                name = name.removeSuffix("\\"),
                parent = parent?.absolutePathString().orEmpty(),
                size = fileSize(),
                lastModifier = getLastModifiedTime().toMillis(),
                isHidden = false,
            )
        }
}

@AssistedInject
internal class DeviceFileClient(
    @Assisted bookshelf: DeviceStorage,
    fileReaderFactoryMap: FileReaderFactoryMap,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : BaseJvmFileClient<DeviceStorage>(bookshelf, fileReaderFactoryMap, dispatcher) {
    @ContributesIntoMap(AppScope::class)
    @FileClientKey(DeviceStorage::class)
    @AssistedFactory
    fun interface Factory : FileClient.Factory<DeviceStorage> {
        override fun create(bookshelf: DeviceStorage): DeviceFileClient
    }
}
