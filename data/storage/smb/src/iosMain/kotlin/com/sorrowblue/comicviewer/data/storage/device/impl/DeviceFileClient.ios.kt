package com.sorrowblue.comicviewer.data.storage.device.impl

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
import dev.zwander.kotlin.file.FileUtils
import dev.zwander.kotlin.file.IPlatformFile
import dev.zwander.kotlin.file.okio.toOkioSource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.buffered
import logcat.logcat
import okio.BufferedSource
import okio.Path.Companion.toPath
import okio.buffer
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import platform.Foundation.NSDirectoryEnumerationSkipsSubdirectoryDescendants
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL

@Factory
@DeviceFileClient
internal actual class DeviceFileClient(
    @InjectedParam override val bookshelf: InternalStorage,
) : FileClient<InternalStorage> {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> {
        val url = NSURL(fileURLWithPath = file.path)
        NSFileManager.defaultManager.contentsOfDirectoryAtURL(
            url = url,
            includingPropertiesForKeys = null,
            options = NSDirectoryEnumerationSkipsSubdirectoryDescendants,
            error = null
        )?.forEach {
            it
        }
        TODO("Not yet implemented")
    }

    override suspend fun exists(path: String): Boolean {
        logcat { "exists(path=$path)" }
        val file = FileUtils.fromString(input = path, !NSURL(fileURLWithPath = path).fileURL) ?: throw FileClientException.InvalidPath()
        return file.getExists()
    }

    override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        val file = FileUtils.fromString(input = path, !NSURL(fileURLWithPath = path).fileURL) ?: throw FileClientException.InvalidPath()
        return file.toFileModel(resolveImageFolder)
    }

    override suspend fun bufferedSource(file: File): BufferedSource {
        val file = FileUtils.fromString(input = file.path, !NSURL(fileURLWithPath = file.path).fileURL) ?: throw FileClientException.InvalidPath()
        return file.openInputStream()!!.toOkioSource()!!.buffer()
    }

    override suspend fun seekableInputStream(file: File): SeekableInputStream {
        TODO("Not yet implemented")
    }

    override suspend fun connect(path: String) {
        logcat { "connect(path=$path)" }
        kotlin.runCatching {
            exists(path)
        }.fold({
            if (!it) {
                throw FileClientException.InvalidPath()
            }
        }) {
            it.printStackTrace()
            when (it) {
                is IllegalArgumentException -> throw FileClientException.InvalidPath()
                else -> throw it
            }
        }
    }

    override suspend fun getAttribute(path: String): FileAttribute? {
        TODO("Not yet implemented")
    }


    private fun IPlatformFile.toFileModel(resolveImageFolder: Boolean = false): File {
        return if (resolveImageFolder && !list { dir, name ->
            name.extension in SUPPORTED_IMAGE }.isNullOrEmpty()
        ) {
            BookFolder(
                path = getAbsolutePath(),
                bookshelfId = bookshelf.id,
                name = getName().removeSuffix("\\"),
                parent = getParentFile()?.getAbsolutePath().orEmpty(),
                size = getLength(),
                lastModifier = getLastModified(),
                isHidden = false,
            )
        } else if (!isDirectory()) {
            BookFile(
                path = getAbsolutePath(),
                bookshelfId = bookshelf.id,
                name = getName().removeSuffix("\\"),
                parent = getParentFile()?.getAbsolutePath().orEmpty(),
                size = getLength(),
                lastModifier = getLastModified(),
                isHidden = false,
            )
        } else {
            Folder(
                path = getAbsolutePath(),
                bookshelfId = bookshelf.id,
                name = getName().removeSuffix("\\"),
                parent = getParentFile()?.getAbsolutePath().orEmpty(),
                size = getLength(),
                lastModifier = getLastModified(),
                isHidden = false,
            )
        }
    }
}
