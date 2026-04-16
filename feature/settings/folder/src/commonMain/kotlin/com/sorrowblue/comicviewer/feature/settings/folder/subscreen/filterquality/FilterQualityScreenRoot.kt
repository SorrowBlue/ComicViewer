package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.filterquality

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.setResult

@Composable
internal fun FilterQualityScreenRoot(
    imageFilterQuality: ImageFilterQuality,
    onDismissRequest: () -> Unit,
) {
    val resultProducer = LocalNavigationResultProducer.current
    FilterQualityScreen(
        currentImageFilterQuality = imageFilterQuality,
        onImageFilterQualityChange = {
            resultProducer.setResult(FilterQualityResultKey, it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}

internal val FilterQualityResultKey: SerializableNavigationResultKey<ImageFilterQuality> =
    SerializableNavigationResultKey(
        serializer = ImageFilterQuality.serializer(),
        resultKey = "FilterQualityResultKey",
    )
