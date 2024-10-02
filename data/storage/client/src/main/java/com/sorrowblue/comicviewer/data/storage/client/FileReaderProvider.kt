package com.sorrowblue.comicviewer.data.storage.client

import android.content.Context
import com.sorrowblue.comicviewer.domain.reader.FileReader
import kotlinx.coroutines.CoroutineDispatcher

interface FileReaderProvider {
    fun get(
        context: Context,
        seekableInputStream: SeekableInputStream,
        dispatcher: CoroutineDispatcher,
    ): FileReader

    val extension: String
}
