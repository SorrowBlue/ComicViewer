package com.sorrowblue.comicviewer.data.coil

import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import okio.Buffer
import okio.BufferedSink

actual suspend fun resizeImage(
    buffer: Buffer,
    sink: BufferedSink,
    imageFormat: ImageFormat,
    quality: Int,
) {
}
