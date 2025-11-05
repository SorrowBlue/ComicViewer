package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import logcat.logcat
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer

@AssistedInject
internal actual class DeviceFileClient(
    @Assisted actual override val bookshelf: InternalStorage,
) : FileClient<InternalStorage> {

    @AssistedFactory
    actual fun interface Factory : FileClient.Factory<InternalStorage> {
        actual override fun create(bookshelf: InternalStorage): DeviceFileClient
    }

    actual override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> {
        logcat { "listFiles(file.path=${file.path}, resolveImageFolder=$resolveImageFolder)" }
        return kotlin.runCatching {
            Files.list(file.path.toPath().toNioPath()).map { it.toFileModel(resolveImageFolder) }
                .toList()
        }.getOrElse {
            it.printStackTrace()
            when (it) {
                is SecurityException -> throw FileClientException.InvalidAuth()
                is IllegalArgumentException -> throw FileClientException.InvalidPath()
                else -> throw it
            }
        }
    }

    actual override suspend fun exists(path: String): Boolean {
        logcat { "exists(path=$path)" }
        return path.toPath().toNioPath().exists()
    }

    actual override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        logcat { "current(path=$path, resolveImageFolder=$resolveImageFolder)" }
        return path.toPath().toNioPath().toFileModel(resolveImageFolder)
    }

    actual override suspend fun bufferedSource(file: File): BufferedSource {
        return FileSystem.SYSTEM.source(file.path.toPath()).buffer()
    }

    actual override suspend fun seekableInputStream(file: File): SeekableInputStream {
        return LocalFileSeekableInputStream(file.path.toPath().toNioPath())
    }

    actual override suspend fun connect(path: String) {
        logcat { "connect(path=$path)" }
        kotlin.runCatching {
            path.toPath().toNioPath().exists()
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

    actual override suspend fun attribute(path: String): FileAttribute {
        TODO("Not yet implemented")
    }

    private fun Path.toFileModel(resolveImageFolder: Boolean = false): File {
        return if (resolveImageFolder && isDirectory() && Files.list(this)
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
}

class LocalFileSeekableInputStream(path: Path) : SeekableInputStream {
    private val file = RandomAccessFile(path.toFile(), "r")

    override fun read(buf: ByteArray): Int {
        return file.read(buf)
    }

    override fun read(): Int {
        return file.read()
    }

    override fun read(b: ByteArray, offset: Int, length: Int): Int {
        return file.read(b, offset, length)
    }

    override fun seek(offset: Long, whence: Int): Long {
        when (whence) {
            SeekableInputStream.SEEK_SET -> file.seek(offset)
            SeekableInputStream.SEEK_CUR -> file.seek(file.filePointer + offset)
            SeekableInputStream.SEEK_END -> file.seek(file.length() + offset)
        }
        return file.filePointer
    }

    override fun position(): Long {
        return file.filePointer
    }

    override fun length(): Long {
        return file.length()
    }

    private var isClosed = false

    override fun isClosed(): Boolean {
        return isClosed
    }

    override fun isEOF(): Boolean {
        return file.length() <= file.filePointer
    }

    override fun close() {
        isClosed = true
        file.close()
    }
}
