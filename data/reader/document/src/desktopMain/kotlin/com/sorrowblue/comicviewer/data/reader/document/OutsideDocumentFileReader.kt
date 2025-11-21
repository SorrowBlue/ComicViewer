package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.plugin.pdf.ISeekableInputStream
import com.sorrowblue.comicviewer.plugin.pdf.PdfFileReader
import com.sorrowblue.comicviewer.plugin.pdf.PdfPlugin

internal object OutsideDocumentFileReader {
    lateinit var plugin: PdfPlugin

    fun getReader(seekableInputStream: ISeekableInputStream, magic: String): PdfFileReader =
        plugin.getReader(seekableInputStream, magic)
}
