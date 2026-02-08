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
import dev.zwander.kotlin.file.FileUtils
import dev.zwander.kotlin.file.IPlatformFile
import dev.zwander.kotlin.file.okio.toOkioSource
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.BufferedSource
import okio.buffer
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsDirectoryKey
import platform.Foundation.NSURLIsHiddenKey
import platform.Foundation.NSURLIsVolumeKey

@AssistedInject
internal actual class DeviceFileClient(@Assisted actual override val bookshelf: InternalStorage) :
    FileClient<InternalStorage> {
    @AssistedFactory
    actual fun interface Factory : FileClient.Factory<InternalStorage> {
        actual override fun create(bookshelf: InternalStorage): DeviceFileClient
    }

    actual override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> {
        return FileUtils.fromString(
            input = file.path,
            isDirectory = !NSURL(fileURLWithPath = file.path).fileURL,
        )
            ?.listFiles()?.map {
                it.toFileModel(resolveImageFolder)
            } ?: throw FileClientException.InvalidPath()
    }

    actual override suspend fun exists(path: String): Boolean {
        return FileUtils.fromString(
            input = path,
            isDirectory = !NSURL(fileURLWithPath = path).fileURL,
        )?.getExists() ?: false
    }

    actual override suspend fun current(path: String, resolveImageFolder: Boolean): File {
        val file =
            FileUtils.fromString(input = path, isDirectory = !NSURL(fileURLWithPath = path).fileURL)
                ?: throw FileClientException.InvalidPath()
        return file.toFileModel(resolveImageFolder)
    }

    actual override suspend fun bufferedSource(file: File): BufferedSource {
        val iPlatformFile =
            FileUtils.fromString(input = file.path, !NSURL(fileURLWithPath = file.path).fileURL)
                ?: throw FileClientException.InvalidPath()
        return iPlatformFile.openInputStream()!!.toOkioSource().buffer()
    }

    actual override suspend fun seekableInputStream(file: File): SeekableInputStream =
        DeviceSeekableInputStream(file.path)

    actual override suspend fun connect(path: String) {
        kotlin
            .runCatching {
                exists(path)
            }.fold({
                if (!it) {
                    throw FileClientException.InvalidPath()
                }
            }) {
                logcat(priority = LogPriority.ERROR) { it.asLog() }
                throw when (it) {
                    is IllegalArgumentException -> FileClientException.InvalidPath()
                    else -> it
                }
            }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual override suspend fun attribute(path: String): FileAttribute {
        val fileManager = NSFileManager.defaultManager
        val url = NSURL.fileURLWithPath(path)

        fun getBoolResource(key: String?): Boolean = memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val result = alloc<ObjCObjectVar<Any?>>()
            if (url.getResourceValue(result.ptr, forKey = key, error = error.ptr)) {
                result.value as? Boolean ?: false
            } else {
                false
            }
        }

        val isDirectory = getBoolResource(NSURLIsDirectoryKey)
        val isHidden = getBoolResource(
            NSURLIsHiddenKey,
        ) || path.substringAfterLast("/").startsWith(".")
        val isVolume = getBoolResource(NSURLIsVolumeKey)

        val isReadonly = !fileManager.isWritableFileAtPath(path)

        val isTemporary = path.contains(NSTemporaryDirectory())

        val isSystem = path.startsWith("/System") ||
            path.startsWith("/usr") ||
            path.startsWith("/bin") ||
            path.startsWith("/sbin")

        val isNormal = !isDirectory && !isSystem && !isHidden && !isReadonly

        return FileAttribute(
            archive = false,
            compressed = false,
            directory = isDirectory,
            normal = isNormal,
            readonly = isReadonly,
            system = isSystem,
            temporary = isTemporary,
            sharedRead = true,
            hidden = isHidden,
            volume = isVolume,
        )
    }

    private fun IPlatformFile.toFileModel(resolveImageFolder: Boolean = false): File {
        val path = getAbsolutePath()
        val name = getName()
        val size = getLength()
        val lastModifiedAtMillis = getLastModified()
        val parent = getParent().orEmpty().removeSuffix("/")
        return if (resolveImageFolder &&
            !list { _, fileName -> fileName.extension in SUPPORTED_IMAGE }.isNullOrEmpty()
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
        } else if (!isDirectory()) {
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
