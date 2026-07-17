/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.Bitmap
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.PageItem
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Composable
internal fun rememberBookScreenState(
    initialUiState: BookScreenUiState.Loaded,
    isCompactWindowClass: Boolean = isCompactWindowClass(),
    viewModel: BookViewModel = assistedMetroViewModel<BookViewModel, BookViewModel.Factory> {
        create(initialUiState.book, initialUiState.collectionId, isCompactWindowClass)
    },
): BookScreenState {
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val currentList = remember { mutableStateListOf<PageItem>() }
    val pagerState = rememberPagerState(
        initialPage = if (initialUiState.alwaysOpenFromFirstPage) {
            1
        } else {
            initialUiState.book.lastPageRead +
                1
        },
        pageCount = { currentList.size },
    )

    DisposableEffect(Unit) {
        onDispose {
            systemUiController.keepScreenOn = false
            systemUiController.screenBrightness = SystemUiController.BRIGHTNESS_OVERRIDE_NONE
        }
    }

    val state = remember(isCompactWindowClass) {
        BookScreenStateImpl(
            initialUiState = initialUiState,
            currentList = currentList,
            pagerState = pagerState,
            coroutineScope = coroutineScope,
            systemUiController = systemUiController,
            bookSettingsFlow = viewModel.bookSettingsFlow,
            viewerSettingsFlow = viewModel.viewerSettingsFlow,
            pageItemListFlow = viewModel.pageItemListFlow,
            updateLastReadPage = viewModel::updateLastReadPage,
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            state.onScreenDispose()
            viewModel.release()
        }
    }
    LifecycleEventEffect(event = Lifecycle.Event.ON_PAUSE, onEvent = state::onStop)
    return state
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
    initialUiState: BookScreenUiState.Loaded,
    override val currentList: SnapshotStateList<PageItem>,
    override val pagerState: PagerState,
    private val coroutineScope: CoroutineScope,
    private val systemUiController: SystemUiController,
    private val bookSettingsFlow: SharedFlow<BookSettings>,
    private val viewerSettingsFlow: SharedFlow<ViewerSettings>,
    private val pageItemListFlow: SharedFlow<List<PageItem>>,
    private val updateLastReadPage: (Int) -> Unit,
) : BookScreenState {
    override var uiState by mutableStateOf(initialUiState)
        private set

    init {
        pageItemListFlow.onEach {
            currentList.addAll(it)
        }.launchIn(coroutineScope)
        bookSettingsFlow.onEach { settings ->
            uiState = uiState.copy(
                bookSheetUiState = uiState.bookSheetUiState.copy(
                    pageScale = mapPageScale(settings.pageScale),
                ),
            )
        }.launchIn(coroutineScope)
        viewerSettingsFlow.onEach { settings ->
            systemUiController.keepScreenOn = settings.keepOnScreen
            if (settings.enableBrightnessControl) {
                systemUiController.screenBrightness = settings.screenBrightness
            }
            uiState = uiState.copy(
                bookSheetUiState = uiState.bookSheetUiState.copy(
                    cutWhitespace = settings.cutWhitespace,
                    beyondViewportPageCount = settings.readAheadPageCount,
                ),
            )
        }.launchIn(coroutineScope)
        coroutineScope.launch {
            if (!uiState.isVisibleTooltip) {
                val settings = viewerSettingsFlow.first()
                if (!settings.showStatusBar) {
                    systemUiController.isStatusBarVisible = false
                }
                if (!settings.showNavigationBar) {
                    systemUiController.isNavigationBarVisible = false
                }
            }
        }
        // Save the initial page position when screen opens
        coroutineScope.launch {
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

    private fun updateLastReadPage() {
        updateLastReadPage(pagerState.currentPage)
    }

    override fun toggleTooltip() {
        val currentVisibleTooltip = uiState.isVisibleTooltip
        uiState = uiState.copy(isVisibleTooltip = !currentVisibleTooltip)
        if (currentVisibleTooltip) {
            coroutineScope.launch {
                val settings = viewerSettingsFlow.first()
                if (!settings.showStatusBar) {
                    systemUiController.isStatusBarVisible = false
                }
                if (!settings.showNavigationBar) {
                    systemUiController.isNavigationBarVisible = false
                }
            }
        } else {
            systemUiController.isSystemBarsVisible = true
        }
    }

    override fun onScreenDispose() {
        systemUiController.isSystemBarsVisible = true
    }

    override fun onStop() {
        coroutineScope.launch {
            updateLastReadPage()
        }
    }

    override fun onPageChange(page: Int) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    private val mutex = Mutex()

    override fun onPageLoad(unratedPage: UnratedPage, bitmap: Bitmap) {
        coroutineScope.launch {
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
        if (index < 0) return

        currentList[index] = if (bitmap.imageWidth < bitmap.imageHeight) {
            BookPage.Split.Single(split.index)
        } else {
            currentList.add(index + 1, BookPage.Split.Left(split.index))
            BookPage.Split.Right(split.index)
        }
    }

    private fun handleSpreadPageLoad(spread: BookPage.Spread.Unrated, bitmap: Bitmap) {
        val index = currentList.indexOf(spread)
        if (index < 0) return

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
                        processSingleSpreadPage(
                            item,
                            index,
                            newList,
                            skipIndex,
                        ).also { nextSingle = it }
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
        // Bounds check to prevent IndexOutOfBoundsException
        if (currentIndex + 1 >= currentList.size) {
            newList.add(item)
            return null
        }

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
