package com.sorrowblue.comicviewer.data.reader.zip.impl

import android.icu.text.Collator
import android.icu.text.RuleBasedCollator
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ZipFileReader
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.logcat
import net.sf.sevenzipjbinding.SevenZip
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem
import okio.BufferedSink
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier

@ZipFileReader
@Factory
internal actual class ZipFileReader(
    @InjectedParam actual val seekableInputStream: SeekableInputStream,
    @Qualifier(ImageExtension::class) supportedException: Set<String>,
    @Qualifier(IoDispatcher::class) private val dispatcher: CoroutineDispatcher,
) : FileReader {

    private val zipFile = SevenZip.openInArchive(null, IInStreamImpl(seekableInputStream))

    private val archive = zipFile.simpleInterface

    private val collator =
        RuleBasedCollator((Collator.getInstance(Locale.US) as RuleBasedCollator).rules).apply {
            strength = Collator.PRIMARY
            numericCollation = true
        }

    private val entries =
        archive.archiveItems.filter { !it.isFolder && it.path.extension() in supportedException }
            .sortedWith(Comparator.comparing(ISimpleInArchiveItem::getPath, collator::compare))

    private val mutex = Mutex()

    override suspend fun fileSize(pageIndex: Int): Long = entries[pageIndex].size ?: 0

    override suspend fun fileName(pageIndex: Int): String = entries[pageIndex].path.orEmpty()

    override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink) {
        mutex.withLock {
            logcat(LogPriority.DEBUG) { "copyTo(pageIndex: $pageIndex) path = ${entries[pageIndex].path}" }
            entries[pageIndex].extractSlow2 {
                logcat(LogPriority.DEBUG) { "  extractSlow2 ${entries[pageIndex].path}" }
                bufferedSink.write(it)
                it.size
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

    private fun String.extension() = substringAfterLast('.', "").lowercase()
}
