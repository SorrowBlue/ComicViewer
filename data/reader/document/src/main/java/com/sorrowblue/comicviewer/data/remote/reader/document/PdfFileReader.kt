package com.sorrowblue.comicviewer.data.remote.reader.document

import android.content.Context
import androidx.annotation.Keep
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream

@Keep
internal class PdfFileReader(context: Context, seekableInputStream: SeekableInputStream) :
    DocumentFileReader(context, "application/pdf", seekableInputStream) {

    interface Factory : FileReader.Factory {
        override fun create(seekableInputStream: SeekableInputStream): PdfFileReader
    }
}
