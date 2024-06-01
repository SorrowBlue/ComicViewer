package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import androidx.annotation.Keep
import com.google.auto.service.AutoService
import com.sorrowblue.comicviewer.data.storage.client.FileReaderProvider
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream

@Suppress("unused")
@Keep
internal class Fb2FileReader(context: Context, seekableInputStream: SeekableInputStream) :
    DocumentFileReader(context, "fb2", seekableInputStream) {

    @AutoService(FileReaderProvider::class)
    class Provider : FileReaderProvider {
        override fun get(context: Context, seekableInputStream: SeekableInputStream) =
            Fb2FileReader(context, seekableInputStream)

        override val extension = "fb2"
    }
}
