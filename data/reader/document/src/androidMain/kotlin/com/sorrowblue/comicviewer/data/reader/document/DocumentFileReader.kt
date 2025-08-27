package com.sorrowblue.comicviewer.data.reader.document

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.net.toUri
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DocumentFileReader
import com.sorrowblue.comicviewer.domain.model.PluginManager
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.plugin.pdf.aidl.IRemotePdfService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.BufferedSink
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier
import com.sorrowblue.comicviewer.plugin.aidl.FileReader as PluginFileReader

@DocumentFileReader
@Factory
internal actual class DocumentFileReader(
    @InjectedParam private val mimeType: String,
    @InjectedParam private val seekableInputStream: SeekableInputStream,
    private val context: Context,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
    private val pluginManager: PluginManager,
) : FileReader, ServiceConnection {

    private val mutex = Mutex()
    private var pdfService: IRemotePdfService? = null
    private var reader: PluginFileReader? = null
    private val job = CoroutineScope(dispatcher)

    init {
        job.launch { mutex.lock() }
        val intent = Intent().apply {
            component = ComponentName(
                "com.sorrowblue.comicviewer.plugin.pdf",
                "com.sorrowblue.comicviewer.plugin.pdf.PdfService"
            )
        }
        context.bindService(intent, this@DocumentFileReader, Context.BIND_AUTO_CREATE)
    }

    actual override suspend fun pageCount(): Int {
        return mutex.withLock {
            withContext(dispatcher) {
                reader?.pageCount() ?: withTimeout(2000) {
                    while (reader == null) {
                        delay(100)
                    }
                    reader!!.pageCount()
                }
            }
        }
    }

    actual override suspend fun fileName(pageIndex: Int): String {
        return ""
    }

    actual override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            withContext(dispatcher) {
                reader?.loadPage(pageIndex, bufferedSink) ?: withTimeout(2000) {
                    while (reader == null) {
                        delay(100)
                    }
                    reader!!.loadPage(pageIndex, bufferedSink)
                }
            }
        }
    }

    actual override fun close() {
        runBlocking {
            withContext(dispatcher) {
                seekableInputStream.close()
                context.unbindService(this@DocumentFileReader)
            }
        }
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        try {
            pdfService = IRemotePdfService.Stub.asInterface(service)
            logcat { "pdfService!!.version=${pdfService!!.version}" }
            job.launch {
                reader =
                    pdfService!!.getFIleReader(
                        SeekableInputStreamImpl(seekableInputStream),
                        mimeType
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
        loadPage(pageIndex).toUri().let(context.contentResolver::openInputStream)
            ?.use { inputStream ->
                bufferedSink.outputStream().use {
                    inputStream.copyTo(it)
                }
            }
    }
}
