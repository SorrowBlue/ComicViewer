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
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.NextBook
import com.sorrowblue.comicviewer.feature.book.section.NextPage
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
): BookScreenState2 = remember {
    BookScreenState2Impl(
        args = args,
        uiState = uiState,
        currentList = currentList,
        pagerState = pagerState,
        scope = scope,
        systemUiController = systemUiController,
        viewModel = viewModel
    )
}

@Stable
internal interface BookScreenState2 {

    val currentList: SnapshotStateList<PageItem>
    val pagerState: PagerState
    val uiState: BookScreenUiState.Loaded

    fun toggleTooltip()
    fun onScreenDispose()
    fun onPageChange(page: Int)
    fun onStop()
    fun onPageLoaded(split: UnratedPage, bitmap: Bitmap)
}

private class BookScreenState2Impl(
    private val args: BookArgs,
    uiState: BookScreenUiState.Loaded,
    override val currentList: SnapshotStateList<PageItem>,
    override val pagerState: PagerState,
    val scope: CoroutineScope,
    val systemUiController: SystemUiController,
    val viewModel: BookViewModel,
) : BookScreenState2 {

    override var uiState by mutableStateOf(uiState)
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
                                BookSettings.PageFormat.Default -> (1..uiState.book.totalPageCount).map {
                                    BookPage.Default(it - 1)
                                }

                                BookSettings.PageFormat.Spread ->
                                    (1..uiState.book.totalPageCount).map {
                                        BookPage.Spread.Unrated(it - 1)
                                    }

                                BookSettings.PageFormat.Split -> (1..uiState.book.totalPageCount).map {
                                    BookPage.Split.Unrated(it - 1)
                                }

                                BookSettings.PageFormat.Auto -> (1..uiState.book.totalPageCount).map {
                                    BookPage.Split.Unrated(it - 1)
                                }
                            }
                        )
                        add(nextPage!!)
                    }
                )
            }.launchIn(scope)
        viewModel.manageBookSettingsUseCase.settings.onEach {
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

    override fun toggleTooltip() {
        uiState = uiState.copy(isVisibleTooltip = !systemUiController.isSystemBarsVisible)
        systemUiController.isSystemBarsVisible = !systemUiController.isSystemBarsVisible
    }

    override fun onScreenDispose() {
        systemUiController.isSystemBarsVisible = true
    }

    override fun onStop() {
        scope.launch {
            val request = UpdateLastReadPageUseCase.Request(
                args.bookshelfId,
                args.path,
                pagerState.currentPage - 1
            )
            viewModel.updateLastReadPageUseCase.execute(request)
        }
    }

    override fun onPageChange(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    override fun onPageLoaded(split: UnratedPage, bitmap: Bitmap) {
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
}
