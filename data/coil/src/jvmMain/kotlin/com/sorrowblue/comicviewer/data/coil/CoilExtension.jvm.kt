package com.sorrowblue.comicviewer.data.coil

import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.impl.use

internal actual suspend fun resizeImage(
    source: Source,
    sink: Sink,
    imageFormat: ImageFormat,
    quality: Int,
) {
    val image = Image.makeFromEncoded(source.readByteArray())
    val data = image.encodeToData(imageFormat.toCompressFormat(), quality)
    data?.use { sink.write(it.bytes) }
}

private fun ImageFormat.toCompressFormat() = when (this) {
    ImageFormat.WEBP -> EncodedImageFormat.WEBP
    ImageFormat.JPEG -> EncodedImageFormat.JPEG
    ImageFormat.PNG -> EncodedImageFormat.PNG
    ImageFormat.ORIGINAL -> EncodedImageFormat.WEBP
}
