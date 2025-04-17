package com.sorrowblue.comicviewer.data.coil

import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import okio.BufferedSink
import okio.BufferedSource
import okio.use
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.impl.use

actual suspend fun resizeImage(
    source: BufferedSource,
    sink: BufferedSink,
    imageFormat: ImageFormat,
    quality: Int,
) {
    val image = Image.makeFromEncoded(source.use { it.readByteArray() })
    val data = image.encodeToData(imageFormat.toCompressFormat(), quality)
    data?.use { sink.write(it.bytes) }
}

private fun ImageFormat.toCompressFormat() = when (this) {
    ImageFormat.WEBP -> EncodedImageFormat.WEBP
    ImageFormat.JPEG -> EncodedImageFormat.JPEG
    ImageFormat.PNG -> EncodedImageFormat.PNG
}
