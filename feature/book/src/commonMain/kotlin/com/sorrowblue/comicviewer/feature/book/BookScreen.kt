package com.sorrowblue.comicviewer.feature.book

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil3.Bitmap
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book as BookFile
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.book.navigation.BookMenuNavKey
import com.sorrowblue.comicviewer.feature.book.section.BookAppBar
import com.sorrowblue.comicviewer.feature.book.section.BookBottomBar
import com.sorrowblue.comicviewer.feature.book.section.BookSheet
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.feature.book.section.PageItem
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile

@NavEdge(BookMenuNavKey::class)
@NavDestination(BookNavKey::class)
@Composable
internal fun BookScreen(
    uiState: BookScreenUiState.Loaded,
    pagerState: PagerState,
    currentList: SnapshotStateList<PageItem>,
    onBackClick: () -> Unit,
    onNextBookClick: (BookFile) -> Unit,
    onContainerClick: () -> Unit,
    onContainerLongClick: () -> Unit,
    onPageChange: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it },
            ) {
                BookAppBar(
                    title = uiState.book.name,
                    onBackClick = onBackClick,
                    onSettingsClick = onSettingsClick,
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                BookBottomBar(
                    pageRange = 1f..uiState.book.totalPageCount.toFloat(),
                    currentPage = pagerState.currentPage,
                    onPageChange = onPageChange,
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier,
    ) { _ ->
        BookSheet(
            uiState = uiState.bookSheetUiState,
            pagerState = pagerState,
            pages = currentList,
            onClick = onContainerClick,
            onLongClick = onContainerLongClick,
            onNextBookClick = onNextBookClick,
            onPageLoad = onPageLoad,
        )
    }
}

@NavPreview(BookNavKey::class, primary = true)
@Preview
@Composable
private fun BookScreenPreview() = PreviewTheme {
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
