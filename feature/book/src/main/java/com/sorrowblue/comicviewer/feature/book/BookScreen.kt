package com.sorrowblue.comicviewer.feature.book

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.section.BookBottomBar
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.BookPager2
import com.sorrowblue.comicviewer.feature.book.section.BookPagerUiState
import com.sorrowblue.comicviewer.feature.book.section.BookSplitPage
import com.sorrowblue.comicviewer.feature.book.section.NextBookSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFaq
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.ElevationTokens
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logcat.logcat

internal sealed interface BookScreenUiState {

    data object Loading : BookScreenUiState

    data class Empty(val book: Book) : BookScreenUiState

    data class Loaded(
        val book: Book,
        val bookPagerUiState: BookPagerUiState,
        val isVisibleTooltip: Boolean,
    ) : BookScreenUiState
}

@Composable
internal fun BookRoute(
    onBackClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    viewModel: BookViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    BookScreen(
        uiState,
        onBackClick = onBackClick,
        onContainerClick = viewModel::toggleTooltip,
        onNextBookClick = onNextBookClick,
        onPageIndexChange = viewModel::updateLastReadPage
    )
}

@Stable
internal class BookScreenState(
    val scope: CoroutineScope,
    val systemUiController: SystemUiController,
)

@Composable
internal fun rememberBookScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    systemUiController: SystemUiController = rememberSystemUiController(),
) = rememberSaveable {
    BookScreenState(scope = scope, systemUiController = systemUiController)
}

@Composable
private fun BookScreen(
    uiState: BookScreenUiState,
    onBackClick: (() -> Unit)?,
    contentPadding: PaddingValues,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = "uiState.book.name",
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
        when (uiState) {

            is BookScreenUiState.Loaded -> TODO()
            is BookScreenUiState.Empty ->
                EmptyContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )

            BookScreenUiState.Loading ->
                LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
        }
    }
}

data class MainContentUiState(
    val book: Book,
    val nextBook: Book?,
    val prevBook: Book?,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContent(
    uiState: MainContentUiState,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onNextBookClick: (Book) -> Unit,
    onClick: () -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 2,
        reverseLayout = true,
        modifier = modifier
    ) { pageIndex ->
        when (val item = pages[pageIndex]) {
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

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ComicIcons.UndrawFaq,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "Couldn't open the book", style = MaterialTheme.typography.headlineSmall)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookScreen(
    uiState: BookScreenUiState,
    onBackClick: () -> Unit,
    onPageIndexChange: (Int) -> Unit,
    onContainerClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sys: SystemUiController = rememberSystemUiController()
    DisposableEffect(Unit) {
        onDispose {
            sys.isSystemBarsVisible = true
        }
    }
    when (uiState) {
        is BookScreenUiState.Loaded -> {
            val currentList = remember(uiState.book.totalPageCount) {
                mutableStateListOf<BookPage>().apply {
                    addAll(getBookPageList(uiState.book.totalPageCount))
                }
            }
            val pagerState = rememberPagerState(
                initialPage = uiState.book.lastPageRead + 1,
                pageCount = { currentList.size }
            )

            sys.isSystemBarsVisible = uiState.isVisibleTooltip
            LaunchedEffect(pagerState.currentPage) {
                if (0 < pagerState.currentPage && pagerState.currentPage < uiState.book.totalPageCount) {
                    onPageIndexChange(pagerState.currentPage - 1)
                }
            }
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
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
                    BookBottomBar(
                        uiState.isVisibleTooltip,
                        when (val a = currentList[pagerState.currentPage]) {
                            is BookPage.Next -> if (a.isNext) uiState.book.totalPageCount else 0
                            is BookPage.Split -> a.index
                        },
                        uiState.book.totalPageCount
                    ) {
                        scope.launch {
                            pagerState.animateScrollToPage(it.toInt())
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .onKeyEvent {
                        logcat("APPAPP") { "onKeyEvent" }
                        if (it.type == KeyEventType.KeyDown) {
                            when (it.key) {
                                Key.VolumeUp -> {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                    true
                                }

                                Key.VolumeDown -> {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                    true
                                }

                                else -> false
                            }
                        } else {
                            false
                        }
                    }
                    .focusable(true)
                    .focusRequester(focusRequester)
            ) {
                BookPager2(
                    uiState = uiState.bookPagerUiState,
                    pagerState = pagerState,
                    currentList = currentList,
                    onClick = onContainerClick,
                    onNextBookClick = onNextBookClick
                )
            }
        }

        BookScreenUiState.Loading -> {
            sys.isStatusBarVisible = true
            sys.isNavigationBarVisible = true
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is BookScreenUiState.Empty -> {
            sys.isStatusBarVisible = true
            sys.isNavigationBarVisible = true
            Scaffold(
                topBar = {
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
            ) {
                PreviewEmpty(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(ComicTheme.dimension.margin)
                )
            }
        }
    }
}

private fun getBookPageList(totalPageCount: Int) =
    (0..<totalPageCount).map {
        when (it) {
            0 -> BookPage.Next(false)
            totalPageCount - 1 -> BookPage.Next(true)
            else -> BookPage.Split(it - 1, BookPage.Split.State.NOT_LOADED)
        }
    }

@Composable
fun PreviewEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ComicIcons.UndrawFaq,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "Couldn't open the book", style = MaterialTheme.typography.headlineSmall)
    }
}
