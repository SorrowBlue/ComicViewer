package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.setResult
import kotlinx.serialization.json.Json

@Composable
fun ImageScaleScreenRoot(imageScale: ImageScale, onDismissRequest: () -> Unit) {
    val resultProducer = LocalNavigationResultProducer.current
    ImageScaleScreen(
        currentImageScale = imageScale,
        onImageScaleChange = {
            resultProducer.setResult(Json, ImageScaleScreenResultKey, it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
