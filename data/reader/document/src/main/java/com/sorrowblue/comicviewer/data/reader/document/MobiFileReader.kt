package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import androidx.annotation.Keep
import com.google.auto.service.AutoService
import com.sorrowblue.comicviewer.data.storage.client.FileReaderProvider
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream

@Suppress("unused")
@Keep
internal class MobiFileReader(context: Context, seekableInputStream: SeekableInputStream) :
    DocumentFileReader(context, "mobi", seekableInputStream) {

    @AutoService(FileReaderProvider::class)
    class Provider : FileReaderProvider {
        override fun get(context: Context, seekableInputStream: SeekableInputStream) =
            MobiFileReader(context, seekableInputStream)

        override val extension = "mobi"
    }
}
