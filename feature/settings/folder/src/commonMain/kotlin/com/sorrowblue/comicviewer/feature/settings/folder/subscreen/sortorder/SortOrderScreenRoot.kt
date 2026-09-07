/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.sortorder

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.result.LocalResultEventBus
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType

@Composable
internal fun SortOrderScreenRoot(sortType: SortType, onDismissRequest: () -> Unit) {
    val resultBus = LocalResultEventBus.current
    SortOrderScreen(
        currentSortType = sortType,
        onFileSortChange = {
            resultBus.sendResult(it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}
