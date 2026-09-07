/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailformat

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.result.LocalResultEventBus
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat

@Composable
internal fun ThumbnailFormatScreenRoot(imageFormat: ImageFormat, onDismissRequest: () -> Unit) {
    val resultBus = LocalResultEventBus.current
    ThumbnailFormatScreen(
        currentImageFormat = imageFormat,
        onImageFormatChange = {
            resultBus.sendResult(it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
