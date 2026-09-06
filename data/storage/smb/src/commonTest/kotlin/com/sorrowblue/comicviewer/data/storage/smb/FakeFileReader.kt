/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.FileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderType
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.io.Sink
import kotlinx.io.Source

@AssistedInject
internal class FakeFileReader(@Assisted private val seekableInputStream: SeekableInputStream) :
    FileReader {
    override suspend fun pageCount(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun source(pageIndex: Int): Source {
        TODO("Not yet implemented")
    }

    override suspend fun extractTo(pageIndex: Int, sink: Sink) {
        TODO("Not yet implemented")
    }

    override suspend fun fileSize(pageIndex: Int): Long {
        TODO("Not yet implemented")
    }

    override suspend fun fileName(pageIndex: Int): String {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    @ContributesIntoMap(AppScope::class)
    @FileReaderKey(FileReaderType.Zip)
    @AssistedFactory
    fun interface Factory : FileReaderFactory {
        override fun create(seekableInputStream: SeekableInputStream): FakeFileReader
    }
}
