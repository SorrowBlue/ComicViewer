/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailscale

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.result.LocalResultEventBus
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale

@Composable
internal fun ThumbnailScaleScreenRoot(imageScale: ImageScale, onDismissRequest: () -> Unit) {
    val resultBus = LocalResultEventBus.current
    ThumbnailScaleScreen(
        currentImageScale = imageScale,
        onImageScaleChange = {
            resultBus.sendResult(it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
