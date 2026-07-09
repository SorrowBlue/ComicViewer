/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderType
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import com.sorrowblue.kpdfium.PdfDocument
import com.sorrowblue.kpdfium.PdfExtractor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source

@AssistedInject
internal class DocumentFileReader(
    @Assisted private val seekableInputStream: SeekableInputStream,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileReader {

    private val source = RealSeekableSource(seekableInputStream)
    private var document: PdfDocument? = null
    private val mutex = Mutex()

    private suspend fun getDocument() = document ?: PdfExtractor.openDocument(source).also {
        document = it
    }

    override suspend fun pageCount(): Int = mutex.withLock {
        withContext(dispatcher) { getDocument().pageCount }
    }

    override suspend fun source(pageIndex: Int): Source = mutex.withLock {
        withContext(dispatcher) {
            Buffer().also {
                getDocument().getPage(pageIndex).render(sink = it)
            }
        }
    }

    override suspend fun extractTo(pageIndex: Int, sink: Sink) = mutex.withLock {
        withContext(dispatcher) {
            getDocument().getPage(pageIndex).render(sink = sink)
        }
    }

    override suspend fun fileSize(pageIndex: Int): Long = mutex.withLock {
        withContext(dispatcher) {
            getDocument().getPage(pageIndex).pageIndex.toLong()
        }
    }

    override suspend fun fileName(pageIndex: Int): String = mutex.withLock {
        withContext(dispatcher) {
            pageIndex.toString()
        }
    }

    override fun close() {
        runBlocking {
            mutex.withLock {
                withContext(dispatcher) {
                    document?.close()
                    source.close()
                }
            }
        }
    }

    @ContributesIntoMap(AppScope::class)
    @FileReaderKey(FileReaderType.Document)
    @AssistedFactory
    fun interface Factory : FileReaderFactory {
        override fun create(seekableInputStream: SeekableInputStream): DocumentFileReader
    }
}
