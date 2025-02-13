package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ZipFileReader
import com.sorrowblue.comicviewer.domain.reader.FileReader
import com.sorrowblue.comicviewer.domain.reader.asKotlinxIoRawSource
import kotlinx.io.Sink
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip
import okio.use
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@ZipFileReader
@Factory
internal actual class ZipFileReader(
    @InjectedParam actual val seekableInputStream: SeekableInputStream,
) : FileReader {

    val zipFileSystem = FileSystem.SYSTEM.openZip(seekableInputStream.path.toPath())
    val fileSystem = FileSystem.SYSTEM

    val paths = zipFileSystem.listRecursively("/".toPath())
        .filter { zipFileSystem.metadata(it).isRegularFile }
        .toList()

    override fun close() {
        zipFileSystem.close()
        fileSystem.close()
    }

    override suspend fun pageCount(): Int {
        return paths.size
    }

    override suspend fun copyTo(pageIndex: Int, sink: Sink) {
        paths[pageIndex]
        zipFileSystem.source(paths[pageIndex]).buffer().use { source ->
            sink.transferFrom(source.asKotlinxIoRawSource())
        }
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    override suspend fun fileName(pageIndex: Int): String {
        return paths[pageIndex].name
    }
}
