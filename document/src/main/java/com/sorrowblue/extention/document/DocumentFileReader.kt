package com.sorrowblue.extention.document

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.artifex.mupdf.fitz.Document
import com.artifex.mupdf.fitz.android.AndroidDrawDevice
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.min

private val COMPRESS_FORMAT =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.JPEG

@Keep
abstract class DocumentFileReader constructor(
    context: Context,
    mimeType: String,
    private val seekableInputStream: SeekableInputStream,
) : FileReader {

    private val width by lazy {
        val windowManager = ContextCompat.getSystemService(context, WindowManager::class.java)!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            windowManager.currentWindowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
                .run {
                    min(
                        windowMetrics.bounds.width() - (right + left),
                        windowMetrics.bounds.height() - (top + bottom)
                    )
                }
        } else {
            @Suppress("DEPRECATION")
            Point().also(windowManager.defaultDisplay::getSize).run { min(x, y) }
        }
    }

    private val document =
        Document.openDocument(SeekableInputStreamImpl(seekableInputStream), mimeType)

    override fun pageCount(): Int {
        return document.countPages()
    }

    override fun fileName(pageIndex: Int): String {
        return ""
    }

    override fun fileSize(pageIndex: Int): Long {
        return 0
    }


    override suspend fun pageInputStream(pageIndex: Int): InputStream {
        return ByteArrayOutputStream().also {
            AndroidDrawDevice.drawPageFitWidth(document.loadPage(pageIndex), width)
                .compress(COMPRESS_FORMAT, 50, it)
        }.toByteArray().inputStream()
    }

    override fun close() {
        seekableInputStream.close()
    }
}
