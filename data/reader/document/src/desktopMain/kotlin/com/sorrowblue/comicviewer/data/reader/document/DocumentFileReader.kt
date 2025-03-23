package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.DocumentFileReader
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import javax.imageio.ImageIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.BufferedSink
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.PDFRenderer
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier

@DocumentFileReader
@Factory
internal actual class DocumentFileReader(
    @Suppress("UNUSED_PARAMETER") @InjectedParam mimeType: String,
    @InjectedParam private val seekableInputStream: SeekableInputStream,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
) : FileReader {

    private val document = Loader.loadPDF(RandomAccessReadImpl(seekableInputStream))

    override suspend fun pageCount(): Int {
        return document.numberOfPages
    }

    override suspend fun fileName(pageIndex: Int): String {
        return ""
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        return 0
    }

    override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        val renderer = PDFRenderer(document)
        val image = renderer.renderImageWithDPI(pageIndex, 300f)
        withContext(dispatcher) {
            ImageIO.write(image, "png", bufferedSink.outputStream())
        }
    }

    override fun close() {
        // TODO()
    }
}
