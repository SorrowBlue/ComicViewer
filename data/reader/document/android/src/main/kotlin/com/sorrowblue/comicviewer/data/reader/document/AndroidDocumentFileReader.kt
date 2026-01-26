package com.sorrowblue.comicviewer.data.reader.document

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.net.toUri
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.PluginManager
import com.sorrowblue.comicviewer.plugin.aidl.FileReader as PluginFileReader
import com.sorrowblue.comicviewer.plugin.pdf.aidl.IRemotePdfService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.BufferedSink

internal const val PdfPluginPackage = "com.sorrowblue.comicviewer.plugin.pdf"
internal const val PdfPluginService = "com.sorrowblue.comicviewer.plugin.pdf.PdfService"

open class AndroidDocumentFileReader(
    private val mimeType: String,
    private val seekableInputStream: SeekableInputStream,
    private val context: Context,
    private val pluginManager: PluginManager,
    private val dispatcher: CoroutineDispatcher,
) : ServiceConnection {
    private val mutex = Mutex(true)
    private var pdfService: IRemotePdfService? = null
    private var reader: PluginFileReader? = null
    private val job = CoroutineScope(dispatcher)

    init {
        val intent = Intent().apply {
            component = ComponentName(PdfPluginPackage, PdfPluginService)
        }
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    suspend fun pageCount2(): Int = mutex.withLock {
        withContext(dispatcher) {
            requireNotNull(reader).pageCount()
        }
    }

    suspend fun copyTo2(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            withContext(dispatcher) {
                requireNotNull(reader).loadPage(pageIndex, bufferedSink)
            }
        }
    }

    fun close2() {
        runBlocking {
            withContext(dispatcher) {
                seekableInputStream.close()
                context.unbindService(this@AndroidDocumentFileReader)
            }
        }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        try {
            val pdfService = IRemotePdfService.Stub.asInterface(service).also {
                this.pdfService = it
            }
            logcat { "pdfService!!.version=${pdfService.version}" }
            job.launch {
                reader = pdfService.getFIleReader(
                    SeekableInputStreamImpl(seekableInputStream),
                    mimeType,
                )
                mutex.unlock()
            }
        } catch (e: SecurityException) {
            logcat(LogPriority.ERROR, "DocumentFileReader") { e.asLog() }
            pluginManager.onError("PDFプラグインの読み込みに失敗しました。\nPDFプラグインを最新Verに更新してください。")
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        pdfService = null
    }

    private fun PluginFileReader.loadPage(pageIndex: Int, bufferedSink: BufferedSink) {
        loadPage(pageIndex)
            .toUri()
            .let(context.contentResolver::openInputStream)
            ?.use { inputStream ->
                bufferedSink.outputStream().use {
                    inputStream.copyTo(it)
                }
            }
    }
}
