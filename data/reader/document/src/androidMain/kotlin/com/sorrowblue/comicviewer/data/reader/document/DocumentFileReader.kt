package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import android.graphics.Bitmap
import android.view.WindowInsets
import android.view.WindowManager
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DocumentFileReader
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import kotlin.math.min
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.BufferedSink
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier

private val COMPRESS_FORMAT = Bitmap.CompressFormat.WEBP_LOSSY

@Suppress("unused")
@DocumentFileReader
@Factory
internal actual class DocumentFileReader(
    @InjectedParam mimeType: String,
    @InjectedParam private val seekableInputStream: SeekableInputStream,
    context: Context,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
) : FileReader {

    private val width by lazy {
        val windowManager = context.getSystemService(WindowManager::class.java)!!
        val windowMetrics = windowManager.currentWindowMetrics
        windowManager.currentWindowMetrics.windowInsets.getInsetsIgnoringVisibility(
            WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout()
        )
            .run {
                min(
                    windowMetrics.bounds.width() - (right + left),
                    windowMetrics.bounds.height() - (top + bottom)
                )
            }
    }

    private val document =
        Document.openDocument(SeekableInputStreamImpl(seekableInputStream), mimeType)

    private val mutex = Mutex()

    override suspend fun pageCount(): Int {
        return withContext(dispatcher) { document.countPages() }
    }

    override suspend fun fileName(pageIndex: Int): String {
        return ""
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            bufferedSink.outputStream().also {
                AndroidDrawDevice.drawPageFitWidth(document.loadPage(pageIndex), width)
                    .compress(COMPRESS_FORMAT, 75, it)
            }
        }
    }

    override fun close() {
        runBlocking {
            withContext(dispatcher) {
                seekableInputStream.close()
            }
        }
    }
}
