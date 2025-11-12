package com.sorrowblue.comicviewer.feature.book

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import coil3.Bitmap
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.NextBook
import com.sorrowblue.comicviewer.feature.book.section.NextPage
import com.sorrowblue.comicviewer.feature.book.section.PageItem
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Composable
context(context: BookScreenContext)
internal fun rememberBookScreenState(
    uiState: BookScreenUiState.Loaded,
    currentList: SnapshotStateList<PageItem> = remember { mutableStateListOf() },
    pagerState: PagerState = rememberPagerState(
        initialPage = uiState.book.lastPageRead + 1,
        pageCount = { currentList.size },
    ),
    scope: CoroutineScope = rememberCoroutineScope(),
    systemUiController: SystemUiController = rememberSystemUiController(),
): BookScreenState {
    val isCompactWindowClass = isCompactWindowClass()
    return remember(isCompactWindowClass) {
        BookScreenStateImpl(
            isCompactWindowClass = isCompactWindowClass,
            uiState = uiState,
            currentList = currentList,
            pagerState = pagerState,
            scope = scope,
            systemUiController = systemUiController,
            getNextBookUseCase = context.getNextBookUseCase,
            manageBookSettingsUseCase = context.manageBookSettingsUseCase,
            updateLastReadPageUseCase = context.updateLastReadPageUseCase,
        )
    }
}

internal interface BookScreenState {
    val currentList: SnapshotStateList<PageItem>
    val pagerState: PagerState
    val uiState: BookScreenUiState.Loaded

    fun toggleTooltip()

    fun onScreenDispose()

    fun onPageChange(page: Int)

    fun onStop()

    fun onPageLoad(unratedPage: UnratedPage, bitmap: Bitmap)
}

private class BookScreenStateImpl(
    val scope: CoroutineScope,
    val systemUiController: SystemUiController,
    uiState: BookScreenUiState.Loaded,
    isCompactWindowClass: Boolean,
    override val currentList: SnapshotStateList<PageItem>,
    override val pagerState: PagerState,
    getNextBookUseCase: GetNextBookUseCase,
    manageBookSettingsUseCase: ManageBookSettingsUseCase,
    private val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
) : BookScreenState {
    override var uiState by mutableStateOf(uiState)
        private set

    private suspend fun GetNextBookUseCase.execute(isNext: Boolean): NextPage {
        val nextBookList = mutableListOf<NextBook>()
        if (uiState.collectionId != CollectionId()) {
            invoke(
                GetNextBookUseCase.Request(
                    uiState.book.bookshelfId,
                    uiState.book.path,
                    GetNextBookUseCase.Location.Collection(uiState.collectionId),
                    isNext,
                ),
            ).dataOrNull()?.let {
                nextBookList.add(NextBook.Collection(it))
            }
        }
        invoke(
            GetNextBookUseCase.Request(
                uiState.book.bookshelfId,
                uiState.book.path,
                GetNextBookUseCase.Location.Folder,
                isNext,
            ),
        ).dataOrNull()?.let {
            nextBookList.add(NextBook.Folder(it))
        }
        return NextPage(isNext, nextBookList)
    }

    init {
        manageBookSettingsUseCase.settings
            .map { it.pageFormat }
            .distinctUntilChanged()
            .onEach { pageFormat ->
                currentList.addAll(
                    buildList {
                        add(getNextBookUseCase.execute(false))
                        addAll(
                            when (pageFormat) {
                                BookSettings.PageFormat.Default -> (1..uiState.book.totalPageCount)
                                    .map {
                                        BookPage.Default(it - 1)
                                    }

                                BookSettings.PageFormat.Spread ->
                                    (1..uiState.book.totalPageCount).map {
                                        BookPage.Spread.Unrated(it - 1)
                                    }

                                BookSettings.PageFormat.Split -> (1..uiState.book.totalPageCount)
                                    .map {
                                        BookPage.Split.Unrated(it - 1)
                                    }

                                BookSettings.PageFormat.Auto ->
                                    if (isCompactWindowClass) {
                                        (1..uiState.book.totalPageCount).map {
                                            BookPage.Split.Unrated(it - 1)
                                        }
                                    } else {
                                        (1..uiState.book.totalPageCount).map {
                                            BookPage.Spread.Unrated(it - 1)
                                        }
                                    }
                            },
                        )
                        add(getNextBookUseCase.execute(true))
                    },
                )
            }.launchIn(scope)
        manageBookSettingsUseCase.settings
            .onEach {
                this.uiState = this.uiState.copy(
                    bookSheetUiState = this.uiState.bookSheetUiState.copy(
                        pageScale = when (it.pageScale) {
                            BookSettings.PageScale.Fit -> PageScale.Fit
                            BookSettings.PageScale.FillWidth -> PageScale.FillWidth
                            BookSettings.PageScale.FillHeight -> PageScale.FillHeight
                            BookSettings.PageScale.Inside -> PageScale.Inside
                            BookSettings.PageScale.None -> PageScale.None
                            BookSettings.PageScale.FillBounds -> PageScale.FillBounds
                        },
                    ),
                )
            }.launchIn(scope)
        val request = UpdateLastReadPageUseCase.Request(
            uiState.book.bookshelfId,
            uiState.book.path,
            pagerState.currentPage - 1,
        )
        scope.launch {
            updateLastReadPageUseCase(request)
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
        val request = UpdateLastReadPageUseCase.Request(
            uiState.book.bookshelfId,
            uiState.book.path,
            pagerState.currentPage - 1,
        )
        scope.launch {
            updateLastReadPageUseCase(request)
        }
    }

    override fun onPageChange(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    val mutex = Mutex()

    override fun onPageLoad(unratedPage: UnratedPage, bitmap: Bitmap) {
        scope.launch {
            mutex.withLock {
                when (unratedPage) {
                    is BookPage.Spread.Unrated -> onSpreadPageLoad(unratedPage, bitmap)

                    is BookPage.Split.Unrated -> onSplitPageLoad(unratedPage, bitmap)
                }
            }
        }
    }

    private fun onSplitPageLoad(split: BookPage.Split.Unrated, bitmap: Bitmap) {
        val index = currentList.indexOf(split)
        if (0 < index) {
            if (bitmap.imageWidth < bitmap.imageHeight) {
                currentList[index] = BookPage.Split.Single(split.index)
            } else {
                currentList[index] = BookPage.Split.Right(split.index)
                currentList.add(index + 1, BookPage.Split.Left(split.index))
            }
        }
    }

    private fun onSpreadPageLoad(spread: BookPage.Spread.Unrated, bitmap: Bitmap) {
        val index = currentList.indexOf(spread)
        if (bitmap.imageWidth < bitmap.imageHeight) {
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
