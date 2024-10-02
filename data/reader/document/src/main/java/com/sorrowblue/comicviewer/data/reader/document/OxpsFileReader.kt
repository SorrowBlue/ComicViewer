package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import androidx.annotation.Keep
import com.google.auto.service.AutoService
import com.sorrowblue.comicviewer.data.storage.client.FileReaderProvider
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import kotlinx.coroutines.CoroutineDispatcher

@Suppress("unused")
@Keep
internal class OxpsFileReader(
    context: Context,
    seekableInputStream: SeekableInputStream,
    dispatcher: CoroutineDispatcher,
) : DocumentFileReader(context, "oxps", seekableInputStream, dispatcher) {

    @AutoService(FileReaderProvider::class)
    class Provider : FileReaderProvider {
        override fun get(
            context: Context,
            seekableInputStream: SeekableInputStream,
            dispatcher: CoroutineDispatcher,
        ) = OxpsFileReader(context, seekableInputStream, dispatcher)

        override val extension = "oxps"
    }
}
