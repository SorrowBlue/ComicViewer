package com.sorrowblue.comicviewer.domain.reader

import okio.Sink

interface FileReader : AutoCloseable {

    suspend fun pageCount(): Int

    suspend fun copyTo(pageIndex: Int, sink: Sink)

    suspend fun fileSize(pageIndex: Int): Long
    suspend fun fileName(pageIndex: Int): String
}
