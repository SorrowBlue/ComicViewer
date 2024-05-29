package com.sorrowblue.comicviewer.data.reader.zip

import android.icu.text.Collator
import android.icu.text.RuleBasedCollator
import com.sorrowblue.comicviewer.data.storage.client.FileReader_Factory
import com.sorrowblue.comicviewer.domain.reader.FileReader
import com.sorrowblue.comicviewer.data.storage.client.ImageExtension
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Locale
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.sf.sevenzipjbinding.SevenZip
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem

internal class ZipFileReader @AssistedInject constructor(
    @Assisted private val seekableInputStream: SeekableInputStream,
    @ImageExtension supportedException: Set<String>,
) : FileReader {

    @AssistedFactory
    interface Factory : FileReader_Factory {

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

    override suspend fun pageInputStream(pageIndex: Int): InputStream {
        return mutex.withLock {
            val outputStream = ByteArrayOutputStream()
            entries[pageIndex].extractSlow2 {
                outputStream.write(it)
                it.size
            }
            ByteArrayInputStream(outputStream.use(ByteArrayOutputStream::toByteArray))
        }
    }

    override suspend fun pageCount(): Int {
        return entries.size
    }

    override fun close() {
        seekableInputStream.close()
        archive.close()
        zipFile.close()
    }
}

private fun ISimpleInArchiveItem.extractSlow2(function: (data: ByteArray) -> Int) {
    extractSlow { function.invoke(it) }
}

fun String.extension() = substringAfterLast('.', "").lowercase()
