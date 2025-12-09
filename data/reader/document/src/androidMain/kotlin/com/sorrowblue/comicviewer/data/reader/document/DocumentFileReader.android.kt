package com.sorrowblue.comicviewer.data.reader.document

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.PluginManager
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import okio.BufferedSink

internal const val PdfPluginPackage = "com.sorrowblue.comicviewer.plugin.pdf"
internal const val PdfPluginService = "com.sorrowblue.comicviewer.plugin.pdf.PdfService"

@AssistedInject
internal actual class DocumentFileReader(
    context: Context,
    pluginManager: PluginManager,
    @Assisted private val mimeType: String,
    @Assisted private val seekableInputStream: SeekableInputStream,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : AndroidDocumentFileReader(mimeType, seekableInputStream, context, pluginManager, dispatcher),
    FileReader {
    @AssistedFactory
    actual fun interface Factory : FileReaderFactory {
        actual override fun create(
            mimeType: String,
            seekableInputStream: SeekableInputStream,
        ): DocumentFileReader
    }

    init {
        val intent = Intent().apply {
            component = ComponentName(PdfPluginPackage, PdfPluginService)
        }
        context.bindService(intent, this@DocumentFileReader, Context.BIND_AUTO_CREATE)
    }

    actual override suspend fun pageCount(): Int = pageCount2()

    actual override suspend fun fileName(pageIndex: Int): String = ""

    actual override suspend fun fileSize(pageIndex: Int): Long = 0

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        copyTo2(pageIndex, bufferedSink)
    }

    actual override fun close() {
        close2()
    }
}
