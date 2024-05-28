package com.sorrowblue.comicviewer.data.reader

import java.io.Closeable
import java.io.InputStream

interface FileReader : Closeable {

    interface Factory {
        fun create(seekableInputStream: SeekableInputStream): FileReader
    }

    suspend fun pageCount(): Int

    suspend fun pageInputStream(pageIndex: Int): InputStream
    suspend fun fileSize(pageIndex: Int): Long
    suspend fun fileName(pageIndex: Int): String
}
