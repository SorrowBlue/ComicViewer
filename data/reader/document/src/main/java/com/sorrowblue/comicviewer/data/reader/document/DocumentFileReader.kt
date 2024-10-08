package com.sorrowblue.comicviewer.data.reader.document

import android.content.Context
import android.graphics.Bitmap
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.reader.FileReader
import kotlin.math.min
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.Sink
import okio.buffer

private val COMPRESS_FORMAT = Bitmap.CompressFormat.WEBP_LOSSY

@Keep
open class DocumentFileReader(
    context: Context,
    mimeType: String,
    private val seekableInputStream: SeekableInputStream,
    private val dispatcher: CoroutineDispatcher,
) : FileReader {

    private val width by lazy {
        val windowManager = ContextCompat.getSystemService(context, WindowManager::class.java)!!
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

    override suspend fun copyTo(pageIndex: Int, sink: Sink) {
        mutex.withLock {
            sink.buffer().outputStream().use {
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
