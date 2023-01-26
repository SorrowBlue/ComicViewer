package com.sorrowblue.comicviewer.data.remote.reader

import java.io.Closeable
import java.io.InputStream

interface FileReader : Closeable {

    interface Factory {
        fun create(seekableInputStream: SeekableInputStream): FileReader
    }

    fun pageCount(): Int

    suspend fun pageInputStream(pageIndex: Int): InputStream
    fun fileSize(pageIndex: Int): Long
    fun fileName(pageIndex: Int): String
}

