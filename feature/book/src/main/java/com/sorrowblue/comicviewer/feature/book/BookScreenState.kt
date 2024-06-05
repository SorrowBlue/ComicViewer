package com.sorrowblue.comicviewer.feature.book

import android.graphics.Bitmap
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
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings.PageFormat
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.feature.book.section.NextBook
import com.sorrowblue.comicviewer.feature.book.section.NextPage
import com.sorrowblue.comicviewer.feature.book.section.PageFormat2
import com.sorrowblue.comicviewer.feature.book.section.PageItem
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
            val book = viewModel.getBookUseCase.execute(
                GetBookUseCase.Request(
                    args.bookshelfId,
                    args.path
                )
            )
                .first().dataOrNull
            if (book == null) {
                this@BookScreenState.uiState = BookScreenUiState.Error(args.name)
                return@launch
            }
            if (book.totalPageCount <= 0) {
                this@BookScreenState.uiState = BookScreenUiState.Error(args.name)
            } else {
                this@BookScreenState.uiState = BookScreenUiState.Loaded(
                    book = book,
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

@Stable
internal class BookScreenState2(
    private val args: BookArgs,
    uiState: BookScreenUiState.Loaded,
    val currentList: SnapshotStateList<PageItem>,
    val pagerState: PagerState,
    val scope: CoroutineScope,
    val systemUiController: SystemUiController,
    val viewModel: BookViewModel,
) {

    var bookMenuSheetUiState by mutableStateOf(BookMenuSheetUiState())
        private set

    var uiState by mutableStateOf(uiState)
        private set

    private var nextPage: NextPage? = null
    private var previousPage: NextPage? = null

    init {
        viewModel.manageBookSettingsUseCase.settings.map { it.pageFormat }.distinctUntilChanged()
            .onEach { pageFormat ->
                if (nextPage == null) {
                    val favorite = if (args.favoriteId != FavoriteId.Default) {
                        viewModel.getNextBookUseCase.execute(
                            GetNextBookUseCase.Request(
                                args.bookshelfId,
                                args.path,
                                GetNextBookUseCase.Location.Favorite(args.favoriteId),
                                true
                            )
                        ).first().dataOrNull
                    } else {
                        null
                    }
                    val nextBook = viewModel.getNextBookUseCase.execute(
                        GetNextBookUseCase.Request(
                            args.bookshelfId,
                            args.path,
                            GetNextBookUseCase.Location.Folder,
                            true
                        )
                    ).first().dataOrNull
                    nextPage = nextBook?.let {
                        if (favorite != null) {
                            NextPage(
                                listOf(
                                    NextBook.Folder(it),
                                    NextBook.Favorite(favorite)
                                ).toPersistentList()
                            )
                        } else {
                            NextPage(listOf(NextBook.Folder(it)).toPersistentList())
                        }
                    } ?: NextPage(emptyList<NextBook>().toPersistentList())
                }
                if (previousPage == null) {
                    val favorite = if (args.favoriteId != FavoriteId.Default) {
                        viewModel.getNextBookUseCase.execute(
                            GetNextBookUseCase.Request(
                                args.bookshelfId,
                                args.path,
                                GetNextBookUseCase.Location.Favorite(args.favoriteId),
                                false
                            )
                        ).first().dataOrNull
                    } else {
                        null
                    }
                    previousPage = viewModel.getNextBookUseCase.execute(
                        GetNextBookUseCase.Request(
                            args.bookshelfId,
                            args.path,
                            GetNextBookUseCase.Location.Folder,
                            false
                        )
                    ).first().dataOrNull?.let {
                        if (favorite != null) {
                            NextPage(
                                listOf(
                                    NextBook.Folder(it),
                                    NextBook.Favorite(favorite)
                                ).toPersistentList()
                            )
                        } else {
                            NextPage(listOf(NextBook.Folder(it)).toPersistentList())
                        }
                    } ?: NextPage(emptyList<NextBook>().toPersistentList())
                }
                currentList.clear()
                currentList.addAll(
                    buildList {
                        add(previousPage!!)
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
                        add(nextPage!!)
                    }
                )
            }.launchIn(scope)
        viewModel.manageBookSettingsUseCase.settings.onEach {
            bookMenuSheetUiState = bookMenuSheetUiState.copy(
                pageFormat2 = when (it.pageFormat) {
                    PageFormat.Default -> PageFormat2.Default
                    PageFormat.Spread -> PageFormat2.Spread
                    PageFormat.Split -> PageFormat2.Split
                    PageFormat.Auto -> PageFormat2.SplitSpread
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
        scope.launch {
            val request = UpdateLastReadPageUseCase.Request(
                args.bookshelfId,
                args.path,
                pagerState.currentPage - 1
            )
            viewModel.updateLastReadPageUseCase.execute(request)
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
        scope.launch {
            val request = UpdateLastReadPageUseCase.Request(
                args.bookshelfId,
                args.path,
                pagerState.currentPage - 1
            )
            viewModel.updateLastReadPageUseCase.execute(request)
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
        val newList = mutableListOf<PageItem>()
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
                        when (val nextItem = currentList[index1 + 1]) {
                            is BookPage.Spread.Single -> {
                                newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                                skipIndex += index1 + 1
                                nextSingle = null
                            }

                            is BookPage.Spread.Combine -> {
                                newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                                nextSingle = BookPage.Spread.Single(nextItem.nextIndex)
                            }

                            else -> {
                                newList.add(item)
                                nextSingle = null
                            }
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

    fun onChangePageDisplayFormat(pageFormat2: PageFormat2) {
        scope.launch {
            viewModel.manageBookSettingsUseCase.edit {
                it.copy(
                    pageFormat = when (pageFormat2) {
                        PageFormat2.Default -> PageFormat.Default
                        PageFormat2.Split -> PageFormat.Split
                        PageFormat2.Spread -> PageFormat.Spread
                        PageFormat2.SplitSpread -> PageFormat.Auto
                    }
                )
            }
        }
    }

    fun onChangePageScale(pageScale: PageScale) {
        scope.launch {
            viewModel.manageBookSettingsUseCase.edit {
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

@Composable
internal fun rememberBookScreenState2(
    args: BookArgs,
    uiState: BookScreenUiState.Loaded,
    currentList: SnapshotStateList<PageItem> = remember { mutableStateListOf() },
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
