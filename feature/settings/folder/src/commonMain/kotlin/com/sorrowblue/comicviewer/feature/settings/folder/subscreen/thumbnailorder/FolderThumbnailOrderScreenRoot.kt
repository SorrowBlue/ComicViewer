/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.thumbnailorder

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.result.LocalResultEventBus
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder

@Composable
internal fun FolderThumbnailOrderScreenRoot(
    folderThumbnailOrder: FolderThumbnailOrder,
    onDismissRequest: () -> Unit,
) {
    val resultBus = LocalResultEventBus.current
    FolderThumbnailOrderScreen(
        currentFolderThumbnailOrder = folderThumbnailOrder,
        onFolderThumbnailOrderChange = {
            resultBus.sendResult(it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
