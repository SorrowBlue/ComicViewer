package com.sorrowblue.comicviewer.data.coil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import io.ktor.utils.io.streams.inputStream
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.asOutputStream

actual suspend fun resizeImage(source: Source, sink: Sink, imageFormat: ImageFormat, quality: Int) {
    BitmapFactory.decodeStream(source.inputStream()).let { bitmap ->
        bitmap.compress(imageFormat.toCompressFormat(), quality, sink.asOutputStream())
        bitmap.recycle()
    }
}

private fun ImageFormat.toCompressFormat() = when (this) {
    ImageFormat.WEBP -> Bitmap.CompressFormat.WEBP_LOSSY
    ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG
    ImageFormat.PNG -> Bitmap.CompressFormat.PNG
}
