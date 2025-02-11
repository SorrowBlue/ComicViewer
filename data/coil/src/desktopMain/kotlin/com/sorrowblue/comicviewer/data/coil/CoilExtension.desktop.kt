package com.sorrowblue.comicviewer.data.coil

import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import kotlinx.io.Sink
import kotlinx.io.Source

actual suspend fun resizeImage(source: Source, sink: Sink, imageFormat: ImageFormat, quality: Int) {
    sink.transferFrom(source)
}
