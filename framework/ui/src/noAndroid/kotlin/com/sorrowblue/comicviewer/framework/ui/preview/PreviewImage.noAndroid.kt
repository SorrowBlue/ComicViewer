package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import coil3.Canvas
import coil3.Image
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler

@OptIn(ExperimentalCoilApi::class)
internal actual val provideAsyncImagePreviewHandler: ProvidedValue<AsyncImagePreviewHandler>
    @Composable
    get() {
        // Do nothing
        return LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler.Default
    }

internal actual class PreviewImage : Image {
    actual override val shareable: Boolean
        get() = false
    actual override val size: Long
        get() = 0
    actual override val width: Int
        get() = 0
    actual override val height: Int
        get() = 0

    actual override fun draw(canvas: Canvas) {
        // Do nothing
    }
}
