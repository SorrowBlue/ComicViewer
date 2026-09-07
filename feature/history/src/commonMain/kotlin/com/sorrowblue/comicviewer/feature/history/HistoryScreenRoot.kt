/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation3.runtime.result.ResultEffect
import com.sorrowblue.comicviewer.domain.model.file.Book

@Composable
internal fun HistoryScreenRoot(
    onDeleteAllClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onBookInfoClick: (Book) -> Unit,
) {
    val state = rememberHistoryScreenState()
    state.scaffoldState.HistoryScreen(
        lazyPagingItems = state.lazyPagingItems,
        lazyGridState = state.lazyGridState,
        onDeleteAllClick = onDeleteAllClick,
        onSettingsClick = onSettingsClick,
        onBookClick = onBookClick,
        onBookInfoClick = onBookInfoClick,
        modifier = Modifier.testTag("HistoryScreenRoot"),
    )
    ResultEffect<ClearAllHistoryScreenResult>(onResult = state::onNavResult)
}
