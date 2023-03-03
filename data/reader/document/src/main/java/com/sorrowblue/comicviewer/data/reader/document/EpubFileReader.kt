package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import androidx.annotation.Keep
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream

@Keep
internal class EpubFileReader(context: Context, seekableInputStream: SeekableInputStream) :
    DocumentFileReader(context, "application/epub+zip", seekableInputStream) {

    interface Factory : FileReader.Factory {
        override fun create(seekableInputStream: SeekableInputStream): EpubFileReader
    }
}
