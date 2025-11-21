package com.sorrowblue.comicviewer.plugin.pdf

internal interface PdfPlugin {
    val version: String
    val timestamp: Long

    fun init()

    fun getReader(seekableInputStream: ISeekableInputStream, magic: String): PdfFileReader
}
