package com.sorrowblue.comicviewer.data.reader.zip.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.reader.FileReader

internal expect class ZipFileReader : FileReader {
    val seekableInputStream: SeekableInputStream
}
