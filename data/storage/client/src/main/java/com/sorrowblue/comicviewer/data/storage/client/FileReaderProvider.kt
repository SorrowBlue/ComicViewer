package com.sorrowblue.comicviewer.data.storage.client

import android.content.Context
import com.sorrowblue.comicviewer.domain.reader.FileReader

interface FileReaderProvider {
    fun get(context: Context, seekableInputStream: SeekableInputStream): FileReader
    val extension: String
}
