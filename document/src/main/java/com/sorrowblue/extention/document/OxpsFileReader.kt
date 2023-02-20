package com.sorrowblue.extention.document

import android.content.Context
import androidx.annotation.Keep
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream

@Keep
internal class OxpsFileReader(
    context: Context,
    seekableInputStream: SeekableInputStream
) : DocumentFileReader(context, "application/oxps", seekableInputStream) {

    interface Factory : FileReader.Factory {
        override fun create(seekableInputStream: SeekableInputStream): OxpsFileReader
    }
}
