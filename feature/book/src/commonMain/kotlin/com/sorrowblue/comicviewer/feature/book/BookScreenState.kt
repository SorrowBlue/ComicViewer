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
    initialUiState: BookScreenUiState.Loaded,
): BookScreenState {
    val isCompactWindowClass = isCompactWindowClass()
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val currentList = remember { mutableStateListOf<PageItem>() }
    val pagerState = rememberPagerState(
        initialPage = initialUiState.book.lastPageRead + 1,
        pageCount = { currentList.size },
    )

    return remember(isCompactWindowClass) {
        BookScreenStateImpl(
            isCompactWindowClass = isCompactWindowClass,
            initialUiState = initialUiState,
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
    private val isCompactWindowClass: Boolean,
    initialUiState: BookScreenUiState.Loaded,
    override val currentList: SnapshotStateList<PageItem>,
    override val pagerState: PagerState,
    private val scope: CoroutineScope,
    private val systemUiController: SystemUiController,
    private val getNextBookUseCase: GetNextBookUseCase,
    private val manageBookSettingsUseCase: ManageBookSettingsUseCase,
    private val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
) : BookScreenState {
    override var uiState by mutableStateOf(initialUiState)
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

    private suspend fun buildPageList(pageFormat: BookSettings.PageFormat): List<PageItem> =
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
        }

    init {
        manageBookSettingsUseCase.settings
            .map { it.pageFormat }
            .distinctUntilChanged()
            .onEach { pageFormat ->
                currentList.addAll(buildPageList(pageFormat))
            }.launchIn(scope)
        manageBookSettingsUseCase.settings
            .onEach { settings ->
                uiState = uiState.copy(
                    bookSheetUiState = uiState.bookSheetUiState.copy(
                        pageScale = mapPageScale(settings.pageScale),
                    ),
                )
            }.launchIn(scope)
        scope.launch {
            updateLastReadPage()
        }
    }

    private fun mapPageScale(pageScale: BookSettings.PageScale): PageScale = when (pageScale) {
        BookSettings.PageScale.Fit -> PageScale.Fit
        BookSettings.PageScale.FillWidth -> PageScale.FillWidth
        BookSettings.PageScale.FillHeight -> PageScale.FillHeight
        BookSettings.PageScale.Inside -> PageScale.Inside
        BookSettings.PageScale.None -> PageScale.None
        BookSettings.PageScale.FillBounds -> PageScale.FillBounds
    }

    private suspend fun updateLastReadPage() {
        val request = UpdateLastReadPageUseCase.Request(
            uiState.book.bookshelfId,
            uiState.book.path,
            pagerState.currentPage - 1,
        )
        updateLastReadPageUseCase(request)
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
            updateLastReadPage()
        }
    }

    override fun onPageChange(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    private val mutex = Mutex()

    override fun onPageLoad(unratedPage: UnratedPage, bitmap: Bitmap) {
        scope.launch {
            mutex.withLock {
                when (unratedPage) {
                    is BookPage.Spread.Unrated -> handleSpreadPageLoad(unratedPage, bitmap)
                    is BookPage.Split.Unrated -> handleSplitPageLoad(unratedPage, bitmap)
                }
            }
        }
    }

    private fun handleSplitPageLoad(split: BookPage.Split.Unrated, bitmap: Bitmap) {
        val index = currentList.indexOf(split)
        if (index <= 0) return

        currentList[index] = if (bitmap.imageWidth < bitmap.imageHeight) {
            BookPage.Split.Single(split.index)
        } else {
            currentList.add(index + 1, BookPage.Split.Left(split.index))
            BookPage.Split.Right(split.index)
        }
    }

    private fun handleSpreadPageLoad(spread: BookPage.Spread.Unrated, bitmap: Bitmap) {
        val index = currentList.indexOf(spread)
        currentList[index] = if (bitmap.imageWidth < bitmap.imageHeight) {
            BookPage.Spread.Single(spread.index)
        } else {
            BookPage.Spread.Spread2(spread.index)
        }

        updateSpreadPageList()
    }

    private fun updateSpreadPageList() {
        val skipIndex = mutableListOf<Int>()
        val newList = mutableListOf<PageItem>()
        var nextSingle: BookPage.Spread.Single? = null

        currentList.forEachIndexed { index, bookItem ->
            if (skipIndex.contains(index)) return@forEachIndexed

            when (val item = nextSingle ?: bookItem) {
                is BookPage.Spread.Combine -> newList.add(item)
                is BookPage.Spread.Single -> {
                    if (item.index == 0) {
                        newList.add(item)
                        nextSingle = null
                    } else {
                        processSingleSpreadPage(item, index, newList, skipIndex).also { nextSingle = it }
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

    private fun processSingleSpreadPage(
        item: BookPage.Spread.Single,
        currentIndex: Int,
        newList: MutableList<PageItem>,
        skipIndex: MutableList<Int>,
    ): BookPage.Spread.Single? {
        return when (val nextItem = currentList[currentIndex + 1]) {
            is BookPage.Spread.Single -> {
                newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                skipIndex += currentIndex + 1
                null
            }

            is BookPage.Spread.Combine -> {
                newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                BookPage.Spread.Single(nextItem.nextIndex)
            }

            else -> {
                newList.add(item)
                null
            }
        }
    }
}
