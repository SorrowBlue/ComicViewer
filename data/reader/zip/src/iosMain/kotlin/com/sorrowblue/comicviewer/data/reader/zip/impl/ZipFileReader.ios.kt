package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ZipFileReader
import com.sorrowblue.comicviewer.domain.reader.FileReader
import logcat.asLog
import logcat.logcat
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.Sink
import okio.buffer
import okio.openZip
import okio.use
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import platform.Foundation.NSURL

@ZipFileReader
@Factory
internal actual class ZipFileReader(
    @InjectedParam actual val seekableInputStream: SeekableInputStream,
) : FileReader {

    val zipFileSystem = FileSystem.SYSTEM.openZip(NSURL.URLWithString(URLString = seekableInputStream.path)!!.path!!.toPath())
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
            logcat { "path=${it}" }
        }
    }

    init {
        logcat { "ZipFileReader init $zipFileSystem, path=$paths" }
    }

    override fun close() {
        zipFileSystem.close()
        fileSystem.close()
    }

    override suspend fun pageCount(): Int {
        return paths.size
    }

    override suspend fun copyTo(pageIndex: Int, sink: Sink) {
        zipFileSystem.source(paths[pageIndex]).buffer().use { source ->
            source.readAll(sink)
        }
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    override suspend fun fileName(pageIndex: Int): String {
        return paths[pageIndex].name
    }
}
