package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.setResult
import kotlinx.serialization.json.Json

@Composable
fun ImageFilterQualityScreenRoot(
    imageFilterQuality: ImageFilterQuality,
    onDismissRequest: () -> Unit,
) {
    val resultProducer = LocalNavigationResultProducer.current
    ImageFilterQualityScreen(
        currentImageFilterQuality = imageFilterQuality,
        onImageFilterQualityChange = {
            resultProducer.setResult(Json, ImageFilterQualityScreenResultKey, it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
