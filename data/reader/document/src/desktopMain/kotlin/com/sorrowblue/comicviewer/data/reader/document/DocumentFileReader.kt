package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DocumentFileReader
import com.sorrowblue.comicviewer.domain.reader.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import javax.imageio.ImageIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import okio.Sink
import okio.buffer
import org.apache.pdfbox.cos.COSDocument
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier

@DocumentFileReader
@Factory
internal actual class DocumentFileReader(
    @InjectedParam mimeType: String,
    @InjectedParam private val seekableInputStream: SeekableInputStream,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
) : FileReader {

    private val document = PDDocument(COSDocument(), IRandomAccessSourceImpl(seekableInputStream))

    private val mutex = Mutex()

    override suspend fun pageCount(): Int {
        return withContext(dispatcher) { document.numberOfPages }
    }

    override suspend fun fileName(pageIndex: Int): String {
        return ""
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    override suspend fun copyTo(pageIndex: Int, sink: Sink) {
        val pdfRenderer = PDFRenderer(document)
        val image = pdfRenderer.renderImageWithDPI(pageIndex, 300f) // DPIを指定
        ImageIO.write(image, "png", sink.buffer().outputStream())
    }

    override fun close() {
        document.close()
    }
}
