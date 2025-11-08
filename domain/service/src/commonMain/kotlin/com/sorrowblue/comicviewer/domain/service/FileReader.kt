package com.sorrowblue.comicviewer.domain.service

import okio.BufferedSink

interface FileReader : AutoCloseable {
    suspend fun pageCount(): Int

    suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink)

    suspend fun fileSize(pageIndex: Int): Long

    suspend fun fileName(pageIndex: Int): String
}
