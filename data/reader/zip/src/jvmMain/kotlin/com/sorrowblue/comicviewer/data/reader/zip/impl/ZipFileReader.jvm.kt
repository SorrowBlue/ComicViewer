package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderType
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import com.sorrowblue.comicviewer.framework.common.annotation.VisibleForAssistedInject
import com.sorrowblue.kioarch.ArchiveEntry
import com.sorrowblue.kioarch.KioArch
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import java.text.Collator
import java.text.RuleBasedCollator
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source

@VisibleForAssistedInject
@AssistedInject
actual class ZipFileReader(
    @Assisted private val seekableInputStream: SeekableInputStream,
    @ImageExtension supportedException: Set<String>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileReader {
    @ContributesIntoMap(AppScope::class)
    @FileReaderKey(FileReaderType.Zip)
    @AssistedFactory
    actual fun interface Factory : FileReaderFactory {
        actual override fun create(
            seekableInputStream: SeekableInputStream,
        ): ZipFileReader
    }

    private val zipFile = KioArch.createReader(IInStreamImpl(seekableInputStream))

    private val archive = zipFile.getEntries()

    private val collator =
        RuleBasedCollator((Collator.getInstance(Locale.US) as RuleBasedCollator).rules).apply {
            strength = Collator.PRIMARY
        }

    private val entries =
        archive
            .filter { !it.isDirectory && it.name.extension() in supportedException }
            .sortedWith(Comparator.comparing(ArchiveEntry::name, collator::compare))

    private val mutex = Mutex()

    actual override suspend fun fileSize(pageIndex: Int): Long = entries[pageIndex].size

    actual override suspend fun fileName(pageIndex: Int): String = entries[pageIndex].name

    actual override suspend fun source(pageIndex: Int): Source {
        return mutex.withLock {
            Buffer().also {
                zipFile.extractEntry(entries[pageIndex], sink = it)
            }
        }
    }

    actual override suspend fun extractTo(pageIndex: Int, sink: Sink) {
        mutex.withLock {
            zipFile.extractEntry(entries[pageIndex], sink = sink)
        }
    }

    actual override suspend fun pageCount(): Int = entries.size

    actual override fun close() {
        runBlocking {
            withContext(dispatcher) {
                seekableInputStream.close()
                zipFile.close()
            }
        }
    }

    private fun String.extension() = substringAfterLast('.', "").lowercase()
}
