package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer

@AssistedInject
internal actual class DeviceFileClient(@Assisted actual override val bookshelf: DeviceStorage) :
    FileClient<DeviceStorage> {
    @AssistedFactory
    actual fun interface Factory : FileClient.Factory<DeviceStorage> {
        actual override fun create(bookshelf: DeviceStorage): DeviceFileClient
    }

    actual override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> {
        logcat { "listFiles(file.path=${file.path}, resolveImageFolder=$resolveImageFolder)" }
        return kotlin.runCatching {
            Files
                .list(file.path.toPath().toNioPath())
                .map { it.toFileModel(resolveImageFolder) }
                .toList()
        }.onFailure {
            logcat(LogPriority.ERROR) { it.asLog() }
            throw when (it) {
                is SecurityException -> FileClientException.InvalidAuth()
                is IllegalArgumentException -> FileClientException.InvalidPath()
                else -> it
            }
        }.getOrThrow()
    }

    actual override suspend fun exists(path: String): Boolean {
        logcat { "exists(path=$path)" }
        return path.toPath().toNioPath().exists()
    }

    actual override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        logcat { "current(path=$path, resolveImageFolder=$resolveImageFolder)" }
        return path.toPath().toNioPath().toFileModel(resolveImageFolder)
    }

    actual override suspend fun bufferedSource(file: File): BufferedSource =
        FileSystem.SYSTEM.source(file.path.toPath()).buffer()

    actual override suspend fun seekableInputStream(file: File): SeekableInputStream =
        LocalFileSeekableInputStream(file.path.toPath().toNioPath())

    actual override suspend fun connect(path: String) {
        logcat { "connect(path=$path)" }
        kotlin.runCatching {
            path.toPath().toNioPath().exists()
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

    actual override suspend fun attribute(path: String): FileAttribute {
        TODO("Not yet implemented")
    }

    private fun Path.toFileModel(resolveImageFolder: Boolean = false): File =
        if (resolveImageFolder && isDirectory() && Files
                .list(this)
                .anyMatch { it.name.extension in SUPPORTED_IMAGE }
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

