package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ZipFileReader
import com.sorrowblue.comicviewer.domain.service.FileReader
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import okio.BufferedSink
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Qualifier

@ZipFileReader
@Factory
internal expect class ZipFileReader(
    @InjectedParam seekableInputStream: SeekableInputStream,
    @Qualifier(ImageExtension::class) supportedException: Set<String>,
    @Qualifier(IoDispatcher::class) dispatcher: CoroutineDispatcher,
) : FileReader {
    override suspend fun pageCount(): Int
    override suspend fun copyTo(pageIndex: Int, bufferedSink: BufferedSink)
    override suspend fun fileSize(pageIndex: Int): Long
    override suspend fun fileName(pageIndex: Int): String
    override fun close()
}
