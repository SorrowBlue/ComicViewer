package com.sorrowblue.comicviewer.data.reader.document

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DocumentFileReader
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.pdf.aidl.IOutputStream
import com.sorrowblue.comicviewer.pdf.aidl.IRemotePdfService
import com.sorrowblue.comicviewer.pdf.aidl.ISeekableInputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.BufferedSink
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier

@DocumentFileReader
@Factory
internal actual class DocumentFileReader(
    @InjectedParam mimeType: String,
    @InjectedParam private val seekableInputStream: SeekableInputStream,
    context: Context,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
    private val reader: PdfDocumentFileReader = PdfDocumentFileReader(
        mimeType,
        seekableInputStream,
        context,
        dispatcher
    ),
) : FileReader by reader

internal class PdfDocumentFileReader(
    mimeType: String,
    private val seekableInputStream: SeekableInputStream,
    context: Context,
    private val dispatcher: CoroutineDispatcher,
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
                        AidlSeekableInputStream(seekableInputStream),
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

    override suspend fun pageCount(): Int {
        return mutex.withLock { withContext(dispatcher) { reader!!.pageCount() } }
    }

    override suspend fun fileName(pageIndex: Int): String {
        return ""
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            reader!!.copyTo(pageIndex, bufferedSink.outputStream().asStream())
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

private fun OutputStream.asStream() = object : IOutputStream.Stub() {
    override fun write(b: Int) {
        this@asStream.write(b)
    }

    override fun write2(b: ByteArray) {
        this@asStream.write(b)
    }

    override fun write3(b: ByteArray, off: Int, len: Int) {
        this@asStream.write(b, off, len)
    }

    override fun flush() {
        this@asStream.flush()
    }

    override fun close() {
        this@asStream.close()
    }
}

class AidlSeekableInputStream(private val seekableInputStream: SeekableInputStream) :
    ISeekableInputStream.Stub() {

    override fun read(buf: ByteArray) = seekableInputStream.read(buf)

    override fun seek(offset: Long, whence: Int) = seekableInputStream.seek(offset, whence)

    override fun position() = seekableInputStream.position()

    override fun close() = seekableInputStream.close()
}
