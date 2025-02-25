package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.domain.reader.FileReader
import okio.BufferedSink

internal actual class DocumentFileReader : FileReader {
    override fun close() {
        TODO("Not yet implemented")
    }

    override suspend fun pageCount(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        TODO("Not yet implemented")
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        TODO("Not yet implemented")
    }

    override suspend fun fileName(pageIndex: Int): String {
        TODO("Not yet implemented")
    }
}
