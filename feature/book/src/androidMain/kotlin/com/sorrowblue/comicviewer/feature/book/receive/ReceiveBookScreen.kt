package com.sorrowblue.comicviewer.feature.book.receive

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId.Companion.invoke
import com.sorrowblue.comicviewer.feature.book.BookScreen
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState
import com.sorrowblue.comicviewer.feature.book.navigation.ReceiveBookNavKey
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile

@NavDestination(ReceiveBookNavKey::class)
@Composable
context(context: ReceiveBookScreenContext)
fun ReceiveBookScreenRoot(uri: String, onBackClick: () -> Unit) {
    val state: ReceiveBookScreenState = rememberReceiveBookScreenState(uri = uri)
    if (state.uiState is BookScreenUiState.Loaded) {
        val uiState = state.uiState as BookScreenUiState.Loaded
        BookScreen(
            uiState = uiState,
            pagerState = state.pagerState,
            currentList = state.currentList,
            onBackClick = onBackClick,
            onNextBookClick = { },
            onContainerClick = state::toggleTooltip,
            onContainerLongClick = {},
            onPageChange = state::onPageChange,
            onSettingsClick = {},
            onPageLoad = state::onPageLoaded,
        )
    }
}

@NavPreview(ReceiveBookNavKey::class, primary = true)
@Preview
@Composable
private fun ReceiveBookScreenPreview() = PreviewTheme {
    BookScreen(
        uiState = BookScreenUiState.Loaded(
            book = fakeBookFile(),
            bookSheetUiState = BookSheetUiState(
                book = fakeBookFile(),
            ),
            collectionId = CollectionId(),
            alwaysOpenFromFirstPage = false,
        ),
        pagerState = rememberPagerState { 0 },
        currentList = SnapshotStateList(),
        onBackClick = {},
        onNextBookClick = {},
        onContainerClick = {},
        onContainerLongClick = {},
        onPageChange = {},
        onSettingsClick = {},
        onPageLoad = { _, _ -> },
    )
}
