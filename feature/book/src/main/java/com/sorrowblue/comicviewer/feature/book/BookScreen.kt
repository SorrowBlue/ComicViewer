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
        val bookPagerUiState: BookPagerUiState,
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
    val uiState = state.uiState
    when (uiState) {
        is BookScreenUiState.Loading ->
            LoadingScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                contentPadding = contentPadding
            )

        is BookScreenUiState.Error ->
            ErrorScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                contentPadding = contentPadding
            )

        is BookScreenUiState.Loaded -> {
            val scope = rememberCoroutineScope()
            val pagerState = rememberPagerState(
                initialPage = (uiState.book.lastPageRead) + 1,
                pageCount = { uiState.book.totalPageCount + 2 }
            )
            BookScreen(
                uiState = uiState,
                pagerState = pagerState,
                currentList = state.currentList,
                onBackClick = onBackClick,
                onNextBookClick = onNextBookClick,
                onContainerClick = state::toggleTooltip,
                onPageChange = {
                    scope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                },
                contentPadding = contentPadding,
            )
            DisposableEffect(Unit) {
                onDispose {
                    state.onScreenDispose()
                }
            }
            LifecycleEffect { e ->
                logcat("Compose") { "onLifecycle ${e.name}" }
                if (e == Lifecycle.Event.ON_STOP) {
                    CoroutineScope(context).launch {
                        state.save(pagerState.currentPage - 1)
                    }
                }
            }
            DisposableEffect(Unit) {
                logcat("Compose") { "onLaunch" }
                onDispose {
                    logcat("Compose") { "onDispose" }
                }
            }
        }
    }
}

private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    logcat("exceptionHandler") { throwable.asLog() }
}

private val context = SupervisorJob() + Dispatchers.Main.immediate + exceptionHandler

@Composable
private fun LoadingScreen(
    uiState: BookScreenUiState.Loading,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = uiState.name,
                onBackClick = onBackClick,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        elevation = ElevationTokens.Level2
                    )
                )
            )
        },
        contentWindowInsets = contentPadding.asWindowInsets()
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ErrorScreen(
    uiState: BookScreenUiState.Error,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = uiState.name,
                onBackClick = onBackClick,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        elevation = ElevationTokens.Level2
                    )
                )
            )
        },
        contentWindowInsets = contentPadding.asWindowInsets()
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                imageVector = ComicIcons.UndrawFaq,
                contentDescription = null,
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp)
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = if (uiState.name.isEmpty()) "Unable to open" else "Unable to open \"${uiState.name}\"",
                style = MaterialTheme.typography.headlineSmall
            )
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
        MainContent(
            uiState = uiState.bookPagerUiState,
            currentList = currentList,
            pagerState = pagerState,
            onNextBookClick = onNextBookClick,
            onClick = onContainerClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    uiState: BookPagerUiState,
    currentList: SnapshotStateList<BookPage>,
    pagerState: PagerState,
    onNextBookClick: (Book) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 2,
        reverseLayout = true,
        modifier = modifier.fillMaxSize()
    ) { pageIndex ->
        when (val item = currentList[pageIndex]) {
            is BookPage.Next -> {
                if (item.isNext) {
                    NextBookSheet(uiState.nextBook, true, onClick = onNextBookClick)
                } else {
                    NextBookSheet(uiState.prevBook, false, onClick = onNextBookClick)
                }
            }

            is BookPage.Split -> BookSplitPage(currentList, uiState.book, item, onClick)
        }
    }
}

internal fun getBookPageList(totalPageCount: Int) =
    buildList {
        add(BookPage.Next(false))
        addAll(
            (1..totalPageCount).map {
                BookPage.Split(it - 1, BookPage.Split.State.NOT_LOADED)
            }
        )
        add(BookPage.Next(true))
    }
