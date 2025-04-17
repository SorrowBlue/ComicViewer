package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.domain.service.FileReader
import okio.BufferedSink

internal actual class DocumentFileReader : FileReader {
    actual override fun close() {
        TODO("Not yet implemented")
    }

    actual override suspend fun pageCount(): Int {
        TODO("Not yet implemented")
    }

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        TODO("Not yet implemented")
    }

    actual override suspend fun fileSize(pageIndex: Int): Long {
        TODO("Not yet implemented")
    }

    actual override suspend fun fileName(pageIndex: Int): String {
        TODO("Not yet implemented")
    }
}
