package com.sorrowblue.comicviewer.data.storage.client

import okio.BufferedSink

interface FileReader : AutoCloseable {
    suspend fun pageCount(): Int

    suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink)

    suspend fun fileSize(pageIndex: Int): Long

    suspend fun fileName(pageIndex: Int): String
}
