package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import java.text.Collator
import java.text.RuleBasedCollator
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import net.sf.sevenzipjbinding.SevenZip
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem
import okio.BufferedSink

@AssistedInject
internal actual class ZipFileReader(
    @Assisted mimeType: String,
    @Assisted private val seekableInputStream: SeekableInputStream,
    @ImageExtension supportedException: Set<String>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileReader {

    @AssistedFactory
    actual fun interface Factory : FileReaderFactory {
        actual override fun create(
            mimeType: String,
            seekableInputStream: SeekableInputStream,
        ): ZipFileReader
    }

    private val zipFile = SevenZip.openInArchive(null, IInStreamImpl(seekableInputStream))

    private val archive = zipFile.simpleInterface

    private val collator =
        RuleBasedCollator((Collator.getInstance(Locale.US) as RuleBasedCollator).rules).apply {
            strength = Collator.PRIMARY
        }

    private val entries =
        archive.archiveItems.filter { !it.isFolder && it.path.extension() in supportedException }
            .sortedWith(Comparator.comparing(ISimpleInArchiveItem::getPath, collator::compare))

    private val mutex = Mutex()

    actual override suspend fun fileSize(pageIndex: Int): Long = entries[pageIndex].size ?: 0

    actual override suspend fun fileName(pageIndex: Int): String = entries[pageIndex].path.orEmpty()

    actual override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            entries[pageIndex].extractSlow2 {
                bufferedSink.write(it)
                it.size
            }
        }
    }

    actual override suspend fun pageCount(): Int {
        return entries.size
    }

    actual override fun close() {
        runBlocking {
            withContext(dispatcher) {
                seekableInputStream.close()
                archive.close()
                zipFile.close()
            }
        }
    }

    private fun ISimpleInArchiveItem.extractSlow2(function: (data: ByteArray) -> Int) {
        extractSlow { function.invoke(it) }
    }

    private fun String.extension() = substringAfterLast('.', "").lowercase()
}
