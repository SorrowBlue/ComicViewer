package com.sorrowblue.comicviewer.data.reader.zip

import android.icu.text.Collator
import android.icu.text.RuleBasedCollator
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.domain.reader.FileReader
import com.sorrowblue.comicviewer.domain.service.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import net.sf.sevenzipjbinding.SevenZip
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem
import okio.Sink
import okio.buffer

internal class ZipFileReader @AssistedInject constructor(
    @Assisted private val seekableInputStream: SeekableInputStream,
    @ImageExtension supportedException: Set<String>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileReader {

    @AssistedFactory
    interface Factory : FileReaderFactory {

        override fun create(seekableInputStream: SeekableInputStream): ZipFileReader
    }

    private val zipFile = SevenZip.openInArchive(null, IInStreamImpl(seekableInputStream))

    private val collator = (Collator.getInstance(Locale.getDefault()) as RuleBasedCollator).apply {
        numericCollation = true
        strength = Collator.PRIMARY
    }

    private val archive = zipFile.simpleInterface

    private val entries =
        archive.archiveItems.filter { !it.isFolder && it.path.extension() in supportedException }
            .sortedWith(Comparator.comparing({ it.path }, collator::compare))
    private val mutex = Mutex()

    override suspend fun fileSize(pageIndex: Int): Long = entries[pageIndex].size ?: 0

    override suspend fun fileName(pageIndex: Int): String = entries[pageIndex].path.orEmpty()

    override suspend fun copyTo(pageIndex: Int, sink: Sink) {
        mutex.withLock {
            sink.buffer().use { buffer ->
                entries[pageIndex].extractSlow2 {
                    buffer.write(it)
                    it.size
                }
            }
        }
    }

    override suspend fun pageCount(): Int {
        return entries.size
    }

    override fun close() {
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
}

fun String.extension() = substringAfterLast('.', "").lowercase()
