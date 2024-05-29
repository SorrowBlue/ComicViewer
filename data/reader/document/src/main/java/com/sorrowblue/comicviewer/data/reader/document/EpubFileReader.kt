package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import androidx.annotation.Keep
import com.google.auto.service.AutoService
import com.sorrowblue.comicviewer.data.storage.client.FileReader_Provider
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream

@Suppress("unused")
@Keep
internal class EpubFileReader(context: Context, seekableInputStream: SeekableInputStream) :
    DocumentFileReader(context, "application/epub+zip", seekableInputStream) {

    @AutoService(FileReader_Provider::class)
    class Provider : FileReader_Provider {
        override fun get(context: Context, seekableInputStream: SeekableInputStream) =
            EpubFileReader(context, seekableInputStream)

        override val extension = "epub"
    }
}
