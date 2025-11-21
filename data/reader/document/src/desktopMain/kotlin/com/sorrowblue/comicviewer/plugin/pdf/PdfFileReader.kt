package com.sorrowblue.comicviewer.plugin.pdf

import java.io.OutputStream

internal interface PdfFileReader {
    fun pageCount(): Int

    fun loadPage(pageIndex: Int, outputStream: OutputStream)

    fun fileSize(pageIndex: Int): Long

    fun close()
}
