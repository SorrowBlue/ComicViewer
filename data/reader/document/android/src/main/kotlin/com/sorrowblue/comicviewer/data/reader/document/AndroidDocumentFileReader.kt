package com.sorrowblue.comicviewer.data.reader.document

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.net.toUri
import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderException
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
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
import com.sorrowblue.comicviewer.plugin.aidl.FileReader as PluginFileReader

internal const val PdfPluginPackage = "com.sorrowblue.comicviewer.plugin.pdf"
internal const val PdfPluginService = "com.sorrowblue.comicviewer.plugin.pdf.PdfService"

class AndroidDocumentFileReader(
    private val mimeType: String,
    private val seekableInputStream: SeekableInputStream,
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
) : FileReader {
    private val mutex = Mutex(true)
    private val job = CoroutineScope(dispatcher)
    private var remotePdfService: IRemotePdfService? = null
    private var fileReader: PluginFileReader? = null

    private val connection = RemotePdfConnection()

    private inner class RemotePdfConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            if (remotePdfService != null) {
                return
            }
            try {
                val pdfService = IRemotePdfService.Stub.asInterface(service).also {
                    remotePdfService = it
                }
                logcat { "remotePdfService version=${pdfService.version}" }
                job.launch {
                    fileReader = pdfService.getFIleReader(
                        SeekableInputStreamImpl(seekableInputStream),
                        mimeType,
                    )
                    mutex.unlock()
                }
            } catch (e: SecurityException) {
                logcat(LogPriority.ERROR, "DocumentFileReader") { e.asLog() }
                context.unbindService(this)
                throw FileReaderException.NotSupport(message = "PDFプラグインの読み込みに失敗しました。\nPDFプラグインを最新Verに更新してください。")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            remotePdfService = null
            fileReader = null
        }
    }

    init {
        val intent = Intent().apply {
            component = ComponentName(PdfPluginPackage, PdfPluginService)
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override suspend fun pageCount(): Int = mutex.withLock {
        withContext(dispatcher) {
            requireNotNull(fileReader).pageCount()
        }
    }

    override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            withContext(dispatcher) {
                requireNotNull(fileReader).loadPage(pageIndex, bufferedSink)
            }
        }
    }

    override suspend fun fileSize(pageIndex: Int) = 0L

    override suspend fun fileName(pageIndex: Int) = ""

    override fun close() {
        runBlocking {
            withContext(dispatcher) {
                seekableInputStream.close()
                context.unbindService(connection)
            }
        }
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
