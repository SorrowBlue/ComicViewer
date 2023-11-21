package com.sorrowblue.comicviewer.feature.book

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.settings.History
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.feature.book.navigation.BookArgs
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logcat.logcat

@Stable
internal class BookScreenState(
    args: BookArgs,
    val scope: CoroutineScope,
    val viewModel: BookViewModel,
    uiState: BookScreenUiState = BookScreenUiState.Loading(args.name),
) {

    var uiState by mutableStateOf(uiState)
        private set

    init {
        scope.launch {
            val book = viewModel.getBook(args.bookshelfId, args.path)
            if (book == null) {
                this@BookScreenState.uiState = BookScreenUiState.Error(args.name)
                return@launch
            }
            if (book.totalPageCount <= 0) {
                this@BookScreenState.uiState = BookScreenUiState.Error(args.name)
            } else {
                this@BookScreenState.uiState = BookScreenUiState.Loaded(
                    book = book,
                    nextBook = viewModel.nextBook(GetNextComicRel.NEXT),
                    prevBook = viewModel.nextBook(GetNextComicRel.PREV),
                    isVisibleTooltip = true
                )
            }
        }
    }
}

@Composable
internal fun rememberBookScreenState(
    args: BookArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookViewModel = hiltViewModel(),
) = remember {
    BookScreenState(
        args = args,
        scope = scope,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
internal class BookScreenState2(
    args: BookArgs,
    uiState: BookScreenUiState.Loaded,
    val pagerState: PagerState,
    val scope: CoroutineScope,
    val systemUiController: SystemUiController,
    val viewModel: BookViewModel,
) {

    var uiState by mutableStateOf(uiState)
        private set

    val currentList = mutableStateListOf<BookPage>().apply {
        addAll(getBookPageList(uiState.book.totalPageCount))
    }

    init {
        scope.launch {
            viewModel.updateHistory(
                History(uiState.book.bookshelfId, uiState.book.parent, args.position)
            )
        }
    }

    fun toggleTooltip() {
        uiState = uiState.copy(isVisibleTooltip = !systemUiController.isSystemBarsVisible)
        systemUiController.isSystemBarsVisible = !systemUiController.isSystemBarsVisible
    }

    fun onScreenDispose() {
        systemUiController.isSystemBarsVisible = true
    }

    fun onStop() {
        logcat("Compose") { "start save history" }
        scope.launch {
            measureTimeMillis {
                viewModel.updateLastReadPage(pagerState.currentPage - 1)
            }.also {
                logcat { "updateLastReadPage $it" }
            }
        }
    }

    fun onPageChange(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun rememberBookScreenState2(
    args: BookArgs,
    uiState: BookScreenUiState.Loaded,
    scope: CoroutineScope = rememberCoroutineScope(),
    systemUiController: SystemUiController = rememberSystemUiController(),
    viewModel: BookViewModel = hiltViewModel(),
    pagerState: PagerState = rememberPagerState(
        initialPage = uiState.book.lastPageRead + 1,
        pageCount = { uiState.book.totalPageCount + 2 }
    ),
) = remember {
    BookScreenState2(
        args = args,
        uiState = uiState,
        pagerState = pagerState,
        scope = scope,
        systemUiController = systemUiController,
        viewModel = viewModel
    )
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
