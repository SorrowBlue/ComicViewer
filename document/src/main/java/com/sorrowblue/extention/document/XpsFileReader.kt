package com.sorrowblue.extention.document

import android.content.Context
import androidx.annotation.Keep
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream

@Keep
internal class XpsFileReader(context: Context, seekableInputStream: SeekableInputStream) :
    DocumentFileReader(context, "application/xps", seekableInputStream) {

    interface Factory : FileReader.Factory {
        override fun create(seekableInputStream: SeekableInputStream): XpsFileReader
    }
}
