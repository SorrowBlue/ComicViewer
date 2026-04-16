package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailformat

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.setResult

@Composable
internal fun ThumbnailFormatScreenRoot(imageFormat: ImageFormat, onDismissRequest: () -> Unit) {
    val resultProducer = LocalNavigationResultProducer.current
    ThumbnailFormatScreen(
        currentImageFormat = imageFormat,
        onImageFormatChange = {
            resultProducer.setResult(ThumbnailFormatScreenResultKey, it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}

internal val ThumbnailFormatScreenResultKey = SerializableNavigationResultKey(
    serializer = ImageFormat.serializer(),
    resultKey = "ThumbnailFormatScreenResultKey",
)
