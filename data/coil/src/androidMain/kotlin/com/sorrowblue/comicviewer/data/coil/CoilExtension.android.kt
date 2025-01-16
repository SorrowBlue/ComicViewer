package com.sorrowblue.comicviewer.data.coil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import okio.Buffer
import okio.BufferedSink

actual suspend fun resizeImage(
    buffer: Buffer,
    sink: BufferedSink,
    imageFormat: ImageFormat,
    quality: Int,
) {
    BitmapFactory.decodeStream(buffer.inputStream()).let { bitmap ->
        bitmap.compress(imageFormat.toCompressFormat(), quality, sink.outputStream())
        bitmap.recycle()
    }
}

private fun ImageFormat.toCompressFormat() = when (this) {
    ImageFormat.WEBP -> Bitmap.CompressFormat.WEBP_LOSSY
    ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG
    ImageFormat.PNG -> Bitmap.CompressFormat.PNG
}
