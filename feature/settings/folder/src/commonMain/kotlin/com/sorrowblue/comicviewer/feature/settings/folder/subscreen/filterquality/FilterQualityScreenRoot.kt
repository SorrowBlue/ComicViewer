/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.filterquality

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.result.LocalResultEventBus
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality

@Composable
internal fun FilterQualityScreenRoot(
    imageFilterQuality: ImageFilterQuality,
    onDismissRequest: () -> Unit,
) {
    val resultBus = LocalResultEventBus.current
    FilterQualityScreen(
        currentImageFilterQuality = imageFilterQuality,
        onImageFilterQualityChange = {
            resultBus.sendResult(it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
