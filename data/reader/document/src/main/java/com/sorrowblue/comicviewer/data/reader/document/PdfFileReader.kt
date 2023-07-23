package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import androidx.annotation.Keep
import com.sorrowblue.comicviewer.data.reader.FileReader
import com.sorrowblue.comicviewer.data.reader.SeekableInputStream

@Keep
internal class PdfFileReader(context: Context, seekableInputStream: SeekableInputStream) :
    DocumentFileReader(context, "application/pdf", seekableInputStream) {

    @Keep
    interface Factory : FileReader.Factory {
        override fun create(seekableInputStream: SeekableInputStream): PdfFileReader
    }
}
