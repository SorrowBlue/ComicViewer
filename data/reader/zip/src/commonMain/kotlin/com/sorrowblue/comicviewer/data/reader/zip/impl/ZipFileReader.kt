/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderType
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import com.sorrowblue.kioarch.ArchiveEntry
import com.sorrowblue.kioarch.KioArch
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

internal expect fun List<ArchiveEntry>.sortedByName(): List<ArchiveEntry>

@AssistedInject
internal class ZipFileReader(
    @Assisted private val seekableInputStream: SeekableInputStream,
    @ImageExtension supportedException: Set<String>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileReader {

    @ContributesIntoMap(AppScope::class)
    @FileReaderKey(FileReaderType.Zip)
    @AssistedFactory
    fun interface Factory : FileReaderFactory {
        override fun create(seekableInputStream: SeekableInputStream): ZipFileReader
    }

    private val zipFile = KioArch.createReader(SeekableSourceImpl(seekableInputStream))

    private val archive = zipFile.getEntries()

    private val entries =
        archive
            .filter { !it.isDirectory && it.name.extension() in supportedException }
            .sortedByName()

    private val mutex = Mutex()

    override suspend fun fileSize(pageIndex: Int): Long = entries[pageIndex].size

    override suspend fun fileName(pageIndex: Int): String = entries[pageIndex].name

    override suspend fun source(pageIndex: Int): Source = mutex.withLock {
        Buffer().also {
            zipFile.extractEntry(entries[pageIndex], sink = it)
        }
    }

    override suspend fun extractTo(pageIndex: Int, sink: Sink) = mutex.withLock {
        zipFile.extractEntry(entries[pageIndex], sink = sink)
    }

    override suspend fun pageCount(): Int = entries.size

    override fun close() {
        runBlocking {
            withContext(dispatcher) {
                seekableInputStream.close()
                zipFile.close()
            }
        }
    }

    private fun String.extension() = substringAfterLast('.', "").lowercase()
}
