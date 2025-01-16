package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ZipFileReader
import com.sorrowblue.comicviewer.domain.reader.FileReader
import okio.Sink
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam

@ZipFileReader
@Factory
internal actual class ZipFileReader(
    @InjectedParam actual val seekableInputStream: SeekableInputStream,
) : FileReader {
    override suspend fun pageCount(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun copyTo(pageIndex: Int, sink: Sink) {
        TODO("Not yet implemented")
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        TODO("Not yet implemented")
    }

    override suspend fun fileName(pageIndex: Int): String {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
