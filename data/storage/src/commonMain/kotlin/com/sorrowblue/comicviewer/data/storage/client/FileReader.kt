package com.sorrowblue.comicviewer.data.storage.client

import kotlinx.io.Sink
import kotlinx.io.Source

interface FileReader : AutoCloseable {
    suspend fun pageCount(): Int

    suspend fun source(pageIndex: Int): Source

    suspend fun extractTo(pageIndex: Int, sink: Sink)

    suspend fun fileSize(pageIndex: Int): Long

    suspend fun fileName(pageIndex: Int): String
}
