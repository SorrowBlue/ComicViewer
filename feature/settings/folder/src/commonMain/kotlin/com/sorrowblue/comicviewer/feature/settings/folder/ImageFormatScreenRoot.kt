package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.setResult
import kotlinx.serialization.json.Json

@Composable
fun ImageFormatScreenRoot(imageFormat: ImageFormat, onDismissRequest: () -> Unit) {
    val resultProducer = LocalNavigationResultProducer.current
    ImageFormatScreen(
        currentImageFormat = imageFormat,
        onImageFormatChange = {
            resultProducer.setResult(Json, ImageFormatScreenResultKey, it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest
    )
}
