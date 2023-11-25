package com.sorrowblue.comicviewer.feature.book

import android.graphics.Bitmap
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings.PageFormat
import com.sorrowblue.comicviewer.domain.model.settings.History
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.feature.book.navigation.BookArgs
import com.sorrowblue.comicviewer.feature.book.section.BookItem
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.feature.book.section.NextBook
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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
                    prevBook = viewModel.nextBook(GetNextComicRel.PREV),
                    nextBook = viewModel.nextBook(GetNextComicRel.NEXT),
                    bookSheetUiState = BookSheetUiState(book)
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
    val currentList: SnapshotStateList<BookItem>,
    val pagerState: PagerState,
    val scope: CoroutineScope,
    val systemUiController: SystemUiController,
    val viewModel: BookViewModel,
) {

    var bookMenuSheetUiState by mutableStateOf(BookMenuSheetUiState())
        private set

    var uiState by mutableStateOf(uiState)
        private set

    init {
        scope.launch {
            viewModel.updateHistory(
                History(uiState.book.bookshelfId, uiState.book.parent, args.position)
            )
        }
        viewModel.bookSettings.map { it.pageFormat }.distinctUntilChanged().onEach { pageFormat ->
            currentList.clear()
            currentList.addAll(
                buildList {
                    add(NextBook.Next(uiState.nextBook))
                    addAll(
                        when (pageFormat) {
                            PageFormat.Default -> (1..uiState.book.totalPageCount).map {
                                BookPage.Default(it - 1)
                            }

                            PageFormat.Spread ->
                                (1..uiState.book.totalPageCount).map {
                                    BookPage.Spread.Unrated(it - 1)
                                }

                            PageFormat.Split -> (1..uiState.book.totalPageCount).map {
                                BookPage.Split.Unrated(it - 1)
                            }

                            PageFormat.Auto -> (1..uiState.book.totalPageCount).map {
                                BookPage.Split.Unrated(it - 1)
                            }
                        }
                    )
                    add(NextBook.Prev(uiState.prevBook))
                }
            )
        }.launchIn(scope)
        viewModel.bookSettings.onEach {
            bookMenuSheetUiState = bookMenuSheetUiState.copy(
                pageDisplayFormat = when (it.pageFormat) {
                    PageFormat.Default -> PageDisplayFormat.Default
                    PageFormat.Spread -> PageDisplayFormat.Spread
                    PageFormat.Split -> PageDisplayFormat.Split
                    PageFormat.Auto -> PageDisplayFormat.SplitSpread
                },
                pageScale = when (it.pageScale) {
                    BookSettings.PageScale.Fit -> PageScale.Fit
                    BookSettings.PageScale.FillWidth -> PageScale.FillWidth
                    BookSettings.PageScale.FillHeight -> PageScale.FillHeight
                    BookSettings.PageScale.Inside -> PageScale.Inside
                    BookSettings.PageScale.None -> PageScale.None
                    BookSettings.PageScale.FillBounds -> PageScale.FillBounds
                }
            )
            this.uiState = this.uiState.copy(
                bookSheetUiState = this.uiState.bookSheetUiState.copy(
                    pageScale = when (it.pageScale) {
                        BookSettings.PageScale.Fit -> PageScale.Fit
                        BookSettings.PageScale.FillWidth -> PageScale.FillWidth
                        BookSettings.PageScale.FillHeight -> PageScale.FillHeight
                        BookSettings.PageScale.Inside -> PageScale.Inside
                        BookSettings.PageScale.None -> PageScale.None
                        BookSettings.PageScale.FillBounds -> PageScale.FillBounds
                    }
                )
            )
        }.launchIn(scope)
    }

    fun toggleTooltip() {
        uiState = uiState.copy(isVisibleTooltip = !systemUiController.isSystemBarsVisible)
        systemUiController.isSystemBarsVisible = !systemUiController.isSystemBarsVisible
    }

    fun onScreenDispose() {
        systemUiController.isSystemBarsVisible = true
    }

    fun onStop() {
        scope.launch {
            viewModel.updateLastReadPage(pagerState.currentPage - 1)
        }
    }

    fun onPageChange(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    fun onPageLoaded(split: UnratedPage, bitmap: Bitmap) {
        when (split) {
            is BookPage.Spread.Unrated -> {
                onPageLoaded2(split, bitmap)
            }

            is BookPage.Split.Unrated -> {
                val index = currentList.indexOf(split)
                if (0 < index) {
                    if (bitmap.width < bitmap.height) {
                        currentList[index] = BookPage.Split.Single(split.index)
                    } else {
                        currentList[index] = BookPage.Split.Right(split.index)
                        currentList.add(index + 1, BookPage.Split.Left(split.index))
                    }
                }
            }
        }
    }

    private fun onPageLoaded2(spread: BookPage.Spread.Unrated, bitmap: Bitmap) {
        val index = currentList.indexOf(spread)
        if (bitmap.width < bitmap.height) {
            currentList[index] = BookPage.Spread.Single(spread.index)
        } else {
            // 横
            currentList[index] = BookPage.Spread.Spread2(spread.index)
        }

        val skipIndex = mutableListOf<Int>()
        val newList = mutableListOf<BookItem>()
        var nextSingle: BookPage.Spread.Single? = null
        currentList.forEachIndexed { index1, bookItem ->
            if (skipIndex.contains(index1)) return@forEachIndexed
            when (val item = nextSingle ?: bookItem) {
                is BookPage.Spread.Combine -> newList.add(item)
                is BookPage.Spread.Single -> {
                    if (item.index == 0) {
                        newList.add(item)
                        nextSingle = null
                    } else {
                        val nextItem = currentList[index1 + 1]
                        if (nextItem is BookPage.Spread.Single) {
                            newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                            skipIndex += index1 + 1
                            nextSingle = null
                        } else if (nextItem is BookPage.Spread.Combine) {
                            newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                            nextSingle = BookPage.Spread.Single(nextItem.nextIndex)
                        } else {
                            newList.add(item)
                            nextSingle = null
                        }
                    }
                }

                is BookPage.Spread.Spread2 -> newList.add(item)
                is BookPage.Spread.Unrated -> newList.add(item)
                else -> newList.add(item)
            }
        }
        currentList.clear()
        currentList.addAll(newList)
    }

    fun hideBookMenu() {
        uiState = uiState.copy(isShowBookMenu = false)
    }

    fun onChangePageDisplayFormat(pageDisplayFormat: PageDisplayFormat) {
        scope.launch {
            viewModel.updateBookSettings {
                it.copy(
                    pageFormat = when (pageDisplayFormat) {
                        PageDisplayFormat.Default -> PageFormat.Default
                        PageDisplayFormat.Split -> PageFormat.Split
                        PageDisplayFormat.Spread -> PageFormat.Spread
                        PageDisplayFormat.SplitSpread -> PageFormat.Auto
                    }
                )
            }
        }
    }

    fun onChangePageScale(pageScale: PageScale) {
        scope.launch {
            viewModel.updateBookSettings {
                it.copy(
                    pageScale = when (pageScale) {
                        PageScale.Fit -> BookSettings.PageScale.Fit
                        PageScale.FillHeight -> BookSettings.PageScale.FillHeight
                        PageScale.FillWidth -> BookSettings.PageScale.FillWidth
                        PageScale.Inside -> BookSettings.PageScale.Inside
                        PageScale.None -> BookSettings.PageScale.None
                        PageScale.FillBounds -> BookSettings.PageScale.FillBounds
                    }
                )
            }
        }
    }

    fun onContainerLongClick() {
        uiState = uiState.copy(isShowBookMenu = true)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun rememberBookScreenState2(
    args: BookArgs,
    uiState: BookScreenUiState.Loaded,
    currentList: SnapshotStateList<BookItem> = remember { mutableStateListOf() },
    pagerState: PagerState = rememberPagerState(
        initialPage = uiState.book.lastPageRead + 1,
        pageCount = { currentList.size }
    ),
    scope: CoroutineScope = rememberCoroutineScope(),
    systemUiController: SystemUiController = rememberSystemUiController(),
    viewModel: BookViewModel = hiltViewModel(),
) = remember {
    BookScreenState2(
        args = args,
        uiState = uiState,
        currentList = currentList,
        pagerState = pagerState,
        scope = scope,
        systemUiController = systemUiController,
        viewModel = viewModel
    )
}
