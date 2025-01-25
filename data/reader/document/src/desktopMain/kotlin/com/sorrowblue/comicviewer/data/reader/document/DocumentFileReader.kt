package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DocumentFileReader
import com.sorrowblue.comicviewer.domain.reader.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import javax.imageio.ImageIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import okio.Sink
import okio.buffer
import org.apache.pdfbox.Loader
import org.apache.pdfbox.cos.COSDocument
import org.apache.pdfbox.io.RandomAccessRead
import org.apache.pdfbox.io.RandomAccessReadView
import org.apache.pdfbox.rendering.PDFRenderer
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier

internal class RandomAccessReadImpl(
    private val seekableInputStream: SeekableInputStream,
) : RandomAccessRead {
    override fun close() {
        seekableInputStream.close()
    }

    override fun read(): Int {
        return seekableInputStream.read()
    }

    override fun read(b: ByteArray, offset: Int, length: Int): Int {
        return seekableInputStream.read(b, offset, length)
    }

    override fun getPosition(): Long {
        return seekableInputStream.position()
    }

    override fun seek(position: Long) {
        seekableInputStream.seek(position, 0)
    }

    override fun length(): Long {
        return seekableInputStream.length()
    }

    override fun isClosed(): Boolean {
        return false
    }

    override fun isEOF(): Boolean {
        return false
    }

    override fun createView(startPosition: Long, streamLength: Long): RandomAccessReadView {
        return RandomAccessReadView(this, startPosition, streamLength)
    }
}

@DocumentFileReader
@Factory
internal actual class DocumentFileReader(
    @InjectedParam mimeType: String,
    @InjectedParam private val seekableInputStream: SeekableInputStream,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
) : FileReader {

    private val inMemoryDoc = COSDocument()

    private val document = Loader.loadPDF(RandomAccessReadImpl(seekableInputStream))
//        PDDocument(inMemoryDoc, RandomAccessReadImpl(seekableInputStream))

    private val mutex = Mutex()

    override suspend fun pageCount(): Int {
        return document.numberOfPages
    }

    override suspend fun fileName(pageIndex: Int): String {
        return ""
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    override suspend fun copyTo(pageIndex: Int, sink: Sink) {
        val renderer = PDFRenderer(document)
        val image = renderer.renderImageWithDPI(pageIndex, 300f)
        ImageIO.write(image, "png", sink.buffer().outputStream())
    }

    override fun close() {
    }
}
