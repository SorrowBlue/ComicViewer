package com.sorrowblue.comicviewer.data.storage.smb.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.smb.IosSmbFile
import com.sorrowblue.comicviewer.data.storage.smb.IosSmbFileClient
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.io.files.Path
import okio.BufferedSource

@AssistedInject
internal actual class SmbFileClient(@Assisted actual override val bookshelf: SmbServer) :
    FileClient<SmbServer> {

    @AssistedFactory
    actual interface Factory : FileClient.Factory<SmbServer> {
        actual override fun create(bookshelf: SmbServer): SmbFileClient
    }

    private val iosSmbFileClient = IosSmbFileClient.factory.create(bookshelf)

    actual override suspend fun listFiles(file: File, resolveImageFolder: Boolean): List<File> =
        iosSmbFileClient.listDirectory(file.path).map {
            it.toFileModel(resolveImageFolder)
        }

    actual override suspend fun exists(path: String): Boolean = kotlin.runCatching {
        iosSmbFileClient.current(path)
    }.fold({ true }, { false })

    actual override suspend fun current(path: String, resolveImageFolder: Boolean): File =
        iosSmbFileClient.current(path).toFileModel(resolveImageFolder)

    actual override suspend fun bufferedSource(file: File): BufferedSource =
        iosSmbFileClient.bufferedSource(file)

    actual override suspend fun seekableInputStream(file: File): SeekableInputStream =
        iosSmbFileClient.seekableInputStream(file)

    actual override suspend fun connect(path: String) {
        iosSmbFileClient.connect(path)
    }

    actual override suspend fun attribute(path: String): FileAttribute =
        iosSmbFileClient.attribute(path)

    private suspend fun IosSmbFile.toFileModel(resolveImageFolder: Boolean = false): File {
        if (resolveImageFolder && isDirectory && runCatching {
                iosSmbFileClient.listDirectory(path)
                    .any { !it.isDirectory && it.name.extension in SUPPORTED_IMAGE }
            }.getOrDefault(false)
        ) {
            return BookFolder(
                path = path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(path).parent.toString().replace("\\", "/") + "/",
                size = 0,
                lastModifier = contentModificationDate,
                isHidden = false,
            )
        }
        return if (isDirectory) {
            Folder(
                path = path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(path)
                    .parent
                    ?.toString()
                    .orEmpty()
                    .replace("\\", "/")
                    .removeSuffix("/") + "/",
                size = 0,
                lastModifier = contentModificationDate,
                isHidden = false,
            )
        } else {
            BookFile(
                path = path,
                bookshelfId = this@SmbFileClient.bookshelf.id,
                name = name.removeSuffix("/"),
                parent = Path(path)
                    .parent
                    ?.toString()
                    .orEmpty()
                    .replace("\\", "/")
                    .removeSuffix("/") + "/",
                size = fileSize,
                lastModifier = contentModificationDate,
                isHidden = false,
            )
        }
    }
}
