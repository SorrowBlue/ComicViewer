package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import coil3.Canvas
import coil3.Image
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler

@OptIn(ExperimentalCoilApi::class)
internal expect val provideAsyncImagePreviewHandler: ProvidedValue<AsyncImagePreviewHandler>
    @Composable
    get

internal expect class PreviewImage : Image {
    override val shareable: Boolean
    override val size: Long
    override val width: Int
    override val height: Int
    override fun draw(canvas: Canvas)
}
