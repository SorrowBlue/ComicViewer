package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import logcat.asLog
import logcat.logcat
import okio.BufferedSink
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip
import okio.use
import platform.Foundation.NSURL

@AssistedInject
internal actual class ZipFileReader(
    @Assisted mimeType: String,
    @Assisted private val seekableInputStream: SeekableInputStream,
    @ImageExtension supportedException: Set<String>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileReader {

    @AssistedFactory
    actual fun interface Factory : FileReaderFactory {
        actual override fun create(
            mimeType: String,
            seekableInputStream: SeekableInputStream,
        ): ZipFileReader
    }

    val zipFileSystem = FileSystem.SYSTEM.openZip(
        NSURL.URLWithString(URLString = seekableInputStream.path)!!.path!!.toPath()
    )
    val fileSystem = FileSystem.SYSTEM

    val paths = zipFileSystem.listRecursively("/".toPath())
        .filter { zipFileSystem.metadata(it).isRegularFile }
        .toList()

    init {
        kotlin.runCatching {
            FileSystem.SYSTEM.openZip(NSURL.URLWithString(URLString = seekableInputStream.path)!!.path!!.toPath())
        }.onFailure {
            logcat { "path=${it.asLog()}" }
        }.onSuccess {
            logcat { "path=$it" }
        }
    }

    init {
        logcat { "ZipFileReader init $zipFileSystem, path=$paths" }
    }

    actual override fun close() {
        zipFileSystem.close()
        fileSystem.close()
    }

    actual override suspend fun pageCount(): Int {
        return paths.size
    }

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        zipFileSystem.source(paths[pageIndex]).buffer().use { source ->
            source.readAll(bufferedSink)
        }
    }

    actual override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    actual override suspend fun fileName(pageIndex: Int): String {
        return paths[pageIndex].name
    }
}
