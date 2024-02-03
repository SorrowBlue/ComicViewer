package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import androidx.annotation.Keep
import com.sorrowblue.comicviewer.data.reader.FileReader
import com.sorrowblue.comicviewer.data.reader.SeekableInputStream

@Suppress("unused")
@Keep
internal class OxpsFileReader(
    context: Context,
    seekableInputStream: SeekableInputStream,
) : DocumentFileReader(context, "application/oxps", seekableInputStream) {

    @Keep
    interface Factory : FileReader.Factory {
        override fun create(seekableInputStream: SeekableInputStream): OxpsFileReader
    }
}
