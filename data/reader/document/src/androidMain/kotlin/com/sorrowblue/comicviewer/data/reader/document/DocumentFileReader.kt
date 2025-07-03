package com.sorrowblue.comicviewer.data.reader.document

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.net.toUri
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DocumentFileReader
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.pdf.aidl.IRemotePdfService
import kotlin.time.measureTimedValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import logcat.logcat
import okio.BufferedSink
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier

@DocumentFileReader
@Factory
internal actual class DocumentFileReader(
    @InjectedParam mimeType: String,
    @InjectedParam private val seekableInputStream: SeekableInputStream,
    private val context: Context,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
) : FileReader {

    private val mutex = Mutex()
    private var pdfService: IRemotePdfService? = null
    private var reader: com.sorrowblue.comicviewer.pdf.aidl.FileReader? = null
    private val job = CoroutineScope(dispatcher)
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            pdfService = IRemotePdfService.Stub.asInterface(service)
            job.launch {
                reader =
                    pdfService!!.getFIleReader(
                        SeekableInputStreamImpl(seekableInputStream),
                        mimeType
                    )
                mutex.unlock()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            pdfService = null
        }
    }

    init {
        job.launch { mutex.lock() }
        Intent().apply {
            component = ComponentName(
                "com.sorrowblue.comicviewer.pdf",
                "com.sorrowblue.comicviewer.pdf.PdfService"
            )
            context.bindService(this, connection, Context.BIND_AUTO_CREATE)
        }
    }

    actual override suspend fun pageCount(): Int {
        return mutex.withLock { withContext(dispatcher) { reader!!.pageCount() } }
    }

    actual override suspend fun fileName(pageIndex: Int): String {
        return ""
    }

    actual override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            measureTimedValue {
                context.contentResolver.openInputStream(reader!!.loadPage(pageIndex).toUri())
                    ?.use { inputStream ->
                        bufferedSink.outputStream().use {
                            inputStream.copyTo(it)
                        }
                    }
            }.also {
                logcat { "copyTo(pageIndex: $pageIndex) took ${it.duration.inWholeMilliseconds} ms" }
            }
        }
    }

    actual override fun close() {
        runBlocking {
            withContext(dispatcher) {
                seekableInputStream.close()
                context.unbindService(connection)
            }
        }
    }
}
