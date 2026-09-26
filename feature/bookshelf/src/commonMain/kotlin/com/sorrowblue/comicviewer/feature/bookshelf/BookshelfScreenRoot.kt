/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.framework.ui.adaptive.NavigationReSelectEffect
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
internal fun BookshelfScreenRoot(
    onSettingsClick: () -> Unit,
    onFabClick: () -> Unit,
    onBookshelfClick: (BookshelfId, PathString) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    viewModel: BookshelfViewModel = metroViewModel(),
) {
    val state = rememberBookshelfScreenState()
    val lazyPagingItems = viewModel.bookshelfPagingFlow.collectAsLazyPagingItems()
    NavigationReSelectEffect(state::onNavigationReSelect)
    BookshelfScreen(
        lazyPagingItems = lazyPagingItems,
        lazyGridState = state.lazyGridState,
        onFabClick = onFabClick,
        onSettingsClick = onSettingsClick,
        onBookshelfClick = onBookshelfClick,
        onBookshelfInfoClick = onBookshelfInfoClick,
        modifier = Modifier.testTag("BookshelfScreenRoot"),
    )
}
