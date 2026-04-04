package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.setResult
import kotlinx.serialization.json.Json

@Composable
internal fun ImageFilterQualityScreenRoot(
    imageFilterQuality: ImageFilterQuality,
    onDismissRequest: () -> Unit,
) {
    val resultProducer = LocalNavigationResultProducer.current
    ImageFilterQualityScreen(
        currentImageFilterQuality = imageFilterQuality,
        onImageFilterQualityChange = {
            resultProducer.setResult(Json, ImageFilterQualityResultKey, it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}

internal val ImageFilterQualityResultKey: SerializableNavigationResultKey<ImageFilterQuality> =
    SerializableNavigationResultKey(
        serializer = ImageFilterQuality.serializer(),
        resultKey = "ImageFilterQualityScreenResultKey",
    )
