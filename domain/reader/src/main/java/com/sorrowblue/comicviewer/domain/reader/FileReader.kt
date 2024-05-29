package com.sorrowblue.comicviewer.domain.reader

import java.io.Closeable
import java.io.InputStream

interface FileReader : Closeable {

    suspend fun pageCount(): Int

    suspend fun pageInputStream(pageIndex: Int): InputStream
    suspend fun fileSize(pageIndex: Int): Long
    suspend fun fileName(pageIndex: Int): String
}
