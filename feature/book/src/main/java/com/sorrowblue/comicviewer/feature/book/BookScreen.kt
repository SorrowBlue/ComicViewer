package com.sorrowblue.comicviewer.feature.book

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.navigation.BookArgs
import com.sorrowblue.comicviewer.feature.book.section.BookBottomBar
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.BookPagerUiState
import com.sorrowblue.comicviewer.feature.book.section.BookSplitPage
import com.sorrowblue.comicviewer.feature.book.section.NextBookSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFaq
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.ElevationTokens
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat

internal sealed interface BookScreenUiState {

    data class Loading(val name: String) : BookScreenUiState

    data class Error(val name: String) : BookScreenUiState

    data class Loaded(
        val book: Book,
        val prevBook: Book?,
        val nextBook: Book?,
        val isVisibleTooltip: Boolean,
    ) : BookScreenUiState
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookRoute(
    args: BookArgs,
    onBackClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    contentPadding: PaddingValues,
    state: BookScreenState = rememberBookScreenState(args = args),
) {
    when (val uiState = state.uiState) {
        is BookScreenUiState.Loading ->
            BookLoadingScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                contentPadding = contentPadding
            )

        is BookScreenUiState.Error ->
            BookErrorScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                contentPadding = contentPadding
            )

        is BookScreenUiState.Loaded -> {
            val state2 = rememberBookScreenState2(args, uiState)
            BookScreen(
                uiState = state2.uiState,
                pagerState = state2.pagerState,
                currentList = state2.currentList,
                onBackClick = onBackClick,
                onNextBookClick = onNextBookClick,
                onContainerClick = state2::toggleTooltip,
                onPageChange = state2::onPageChange,
                contentPadding = contentPadding,
            )
            DisposableEffect(Unit) {
                onDispose(state2::onScreenDispose)
            }
            LifecycleEffect(targetEvent = Lifecycle.Event.ON_STOP, action = state2::onStop)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookScreen(
    uiState: BookScreenUiState.Loaded,
    pagerState: PagerState,
    currentList: SnapshotStateList<BookPage>,
    onBackClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    onContainerClick: () -> Unit,
    onPageChange: (Int) -> Unit,
    contentPadding: PaddingValues,
) {
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it }
            ) {
                TopAppBar(
                    title = uiState.book.name,
                    onBackClick = onBackClick,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            elevation = ElevationTokens.Level2
                        )
                    )
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.isVisibleTooltip,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                BookBottomBar(
                    pageRange = 1f..uiState.book.totalPageCount.toFloat(),
                    currentPage = pagerState.currentPage,
                    onPageChange = onPageChange
                )
            }
        },
        contentWindowInsets = contentPadding.asWindowInsets()
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 2,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxSize()
//                .waterfallPadding()
        ) { pageIndex ->
            when (val item = currentList[pageIndex]) {
                is BookPage.Next -> {
                    if (item.isNext) {
                        NextBookSheet(uiState.nextBook, true, onClick = onNextBookClick)
                    } else {
                        NextBookSheet(uiState.prevBook, false, onClick = onNextBookClick)
                    }
                }

                is BookPage.Split -> BookSplitPage(currentList, uiState.book, item, onContainerClick)
            }
        }
    }
}
