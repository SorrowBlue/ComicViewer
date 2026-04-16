package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailscale

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.setResult

@Composable
internal fun ThumbnailScaleScreenRoot(imageScale: ImageScale, onDismissRequest: () -> Unit) {
    val resultProducer = LocalNavigationResultProducer.current
    ThumbnailScaleScreen(
        currentImageScale = imageScale,
        onImageScaleChange = {
            resultProducer.setResult(ThumbnailScaleScreenResultKey, it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}

internal val ThumbnailScaleScreenResultKey = SerializableNavigationResultKey(
    serializer = ImageScale.serializer(),
    resultKey = "ThumbnailScaleScreenResultKey",
)
