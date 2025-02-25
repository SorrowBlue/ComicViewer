package com.sorrowblue.comicviewer.data.storage.device.impl

import androidx.core.uri.UriUtils
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
import dev.zwander.kotlin.file.okio.toOkioSource
import kotlinx.cinterop.ExperimentalForeignApi
import logcat.logcat
import okio.BufferedSource
import okio.FileSystem
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
        val url = NSURL.URLWithString(URLString = file.path)!!
        return NSFileManager.defaultManager.contentsOfDirectoryAtURL(
            url = url,
            includingPropertiesForKeys = null,
            options = NSDirectoryEnumerationSkipsSubdirectoryDescendants,
            error = null
        ).orEmpty().filterIsInstance<NSURL>().map {
            it.toFileModel(resolveImageFolder)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun exists(path: String): Boolean {
        logcat { "exists(path=$path)" }
        val url = NSURL.URLWithString(URLString = path) ?: return false
        return url.checkResourceIsReachableAndReturnError(null)
    }

    override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        val url = NSURL.URLWithString(URLString = path)!!
        return url.toFileModel(resolveImageFolder)
    }

    override suspend fun bufferedSource(file: File): BufferedSource {
        val file = FileUtils.fromString(input = file.path, !NSURL(fileURLWithPath = file.path).fileURL) ?: throw FileClientException.InvalidPath()
        return file.openInputStream()!!.toOkioSource().buffer()
    }

    override suspend fun seekableInputStream(file: File): SeekableInputStream {
        return LocalFileSeekableInputStream(file.path)
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

    override suspend fun attribute(path: String): FileAttribute {
        TODO("Not yet implemented")
    }

    private fun NSURL.toFileModel(resolveImageFolder: Boolean = false): File {
        val path = absoluteString.orEmpty().also {
            logcat { "$it" }
        }
        val name = absoluteString?.toPath()?.name?.let(UriUtils::decode).orEmpty().also {
            logcat { "$it" }
        }
        val size = FileSystem.SYSTEM.metadataOrNull(absoluteString?.toPath()!!)?.size ?: 0
        logcat { "$size" }
        val lastModifiedAtMillis = FileSystem.SYSTEM.metadataOrNull(absoluteString?.toPath()!!)?.lastModifiedAtMillis ?: 0
        logcat { "$lastModifiedAtMillis" }
        val lastPath = if (hasDirectoryPath) {
            lastPathComponent?.let(UriUtils::encode)?.plus("/").orEmpty()
        } else {
            lastPathComponent?.let(UriUtils::encode).orEmpty()
        }
        val parent = absoluteString?.removeSuffix(lastPath).orEmpty()
        val file = FileUtils.fromString(input = path, hasDirectoryPath) ?: throw FileClientException.InvalidPath()
        return if (resolveImageFolder && !file.list { dir, name ->
            name.extension in SUPPORTED_IMAGE }.isNullOrEmpty()
        ) {
            BookFolder(
                path = path,
                bookshelfId = bookshelf.id,
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModifiedAtMillis,
                isHidden = false,
            )
        } else if (!hasDirectoryPath) {
            BookFile(
                path = path,
                bookshelfId = bookshelf.id,
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModifiedAtMillis,
                isHidden = false,
            )
        } else {
            Folder(
                path = path,
                bookshelfId = bookshelf.id,
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModifiedAtMillis,
                isHidden = false,
            )
        }
    }
}


class LocalFileSeekableInputStream(override val path: String) : SeekableInputStream {
    override fun close() {

    }
}
