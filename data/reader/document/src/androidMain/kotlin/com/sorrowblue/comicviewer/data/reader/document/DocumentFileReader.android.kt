package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher

@AssistedInject
internal actual class DocumentFileReader(
    context: Context,
    @Assisted private val mimeType: String,
    @Assisted private val seekableInputStream: SeekableInputStream,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileReader by AndroidDocumentFileReader(mimeType, seekableInputStream, context, dispatcher) {
    @AssistedFactory
    actual fun interface Factory : FileReaderFactory {
        actual override fun create(
            mimeType: String,
            seekableInputStream: SeekableInputStream,
        ): DocumentFileReader
    }
}
