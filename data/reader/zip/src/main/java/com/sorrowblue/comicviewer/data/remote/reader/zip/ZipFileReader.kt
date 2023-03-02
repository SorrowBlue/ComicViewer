package com.sorrowblue.comicviewer.data.remote.reader.zip

import android.icu.text.Collator
import android.icu.text.RuleBasedCollator
import com.sorrowblue.comicviewer.data.common.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.data.common.extension
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Locale
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.sf.sevenzipjbinding.SevenZip

internal class ZipFileReader(private val seekableInputStream: SeekableInputStream) : FileReader {

    private val zipFile = SevenZip.openInArchive(null, IInStreamImpl(seekableInputStream))

    private val collator = (Collator.getInstance(Locale.getDefault()) as RuleBasedCollator).apply {
        numericCollation = true
        strength = Collator.PRIMARY
    }

    private val archive = zipFile.simpleInterface

    private val entries =
        archive.archiveItems.filter { !it.isFolder && it.path.extension in SUPPORTED_IMAGE }
            .sortedWith(Comparator.comparing({ it.path }, collator::compare))
    private val mutex = Mutex()

    override fun fileSize(pageIndex: Int): Long = entries[pageIndex].size ?: 0

    override fun fileName(pageIndex: Int): String = entries[pageIndex].path.orEmpty()

    override suspend fun pageInputStream(pageIndex: Int): InputStream {
        return mutex.withLock {
            val outputStream = ByteArrayOutputStream()
            entries[pageIndex].extractSlow {
                outputStream.write(it)
                it.size
            }
            ByteArrayInputStream(outputStream.use(ByteArrayOutputStream::toByteArray))
        }
    }

    override fun pageCount(): Int {
        return entries.size
    }

    override fun close() {
        seekableInputStream.close()
        archive.close()
        zipFile.close()
    }
}
