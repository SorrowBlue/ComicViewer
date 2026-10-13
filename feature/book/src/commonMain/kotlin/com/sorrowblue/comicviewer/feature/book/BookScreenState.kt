/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import coil3.Bitmap
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun rememberBookScreenState(
    initialUiState: BookScreenUiState.Loaded,
    isCompactWindowClass: Boolean = isCompactWindowClass(),
    viewModel: BookViewModel = assistedMetroViewModel<BookViewModel, BookViewModel.Factory> {
        create(
            book = initialUiState.book,
            collectionId = initialUiState.collectionId,
            isCompactWindowClass = isCompactWindowClass,
        )
    },
): BookScreenState {
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val currentList: SnapshotStateList<PageItem> = retain { mutableStateListOf() }
    val targetPage = if (initialUiState.alwaysOpenFromFirstPage) {
        1
    } else {
        (initialUiState.book.lastPageRead + 1).coerceIn(
            1,
            maxOf(1, initialUiState.book.totalPageCount),
        )
    }
    val pagerState = rememberPagerState(
        initialPage = if (currentList.isNotEmpty()) {
            currentList.findPagerIndex(targetPage, initialUiState.book.totalPageCount)
        } else {
            0
        },
        pageCount = { currentList.size },
    )

    DisposableEffect(Unit) {
        onDispose {
            systemUiController.keepScreenOn = false
            systemUiController.screenBrightness = SystemUiController.BRIGHTNESS_OVERRIDE_NONE
        }
    }

    SideEffect(isCompactWindowClass) {
        viewModel.updateIsCompactWindow(isCompactWindowClass)
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state = remember(lifecycle) {
        BookScreenStateImpl(
            initialUiState = initialUiState,
            currentList = currentList,
            pagerState = pagerState,
            coroutineScope = coroutineScope,
            systemUiController = systemUiController,
            lifecycle = lifecycle,
            bookSettingsFlow = viewModel.bookSettingsFlow,
            viewerSettingsFlow = viewModel.viewerSettingsFlow,
            pageItemListFlow = viewModel.pageItemListFlow,
            updateLastReadPage = viewModel::updateLastReadPage,
            onPageLoaded = viewModel::onPageLoaded,
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            state.onScreenDispose()
            viewModel.release()
        }
    }
    LifecycleEventEffect(event = Lifecycle.Event.ON_PAUSE, onEvent = state::onStop)
    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP, onEvent = state::onStop)
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
    lifecycle: Lifecycle,
    bookSettingsFlow: SharedFlow<BookSettings>,
    private val viewerSettingsFlow: SharedFlow<ViewerSettings>,
    pageItemListFlow: Flow<List<PageItem>>,
    private val updateLastReadPage: (Int) -> Unit,
    private val onPageLoaded: (UnratedPage, Boolean) -> Unit,
) : BookScreenState {
    override var uiState by mutableStateOf(initialUiState)
        private set

    private var isInitialized = false

    init {
        pageItemListFlow
            .flowWithLifecycle(lifecycle)
            .onEach {
                val isFirstLoad = currentList.isEmpty() && it.isNotEmpty()
                val currentItem = currentList.getOrNull(pagerState.currentPage)
                val currentDisplayPage = if (isFirstLoad) {
                    if (initialUiState.alwaysOpenFromFirstPage) {
                        1
                    } else {
                        (initialUiState.book.lastPageRead + 1).coerceIn(
                            1,
                            maxOf(1, initialUiState.book.totalPageCount),
                        )
                    }
                } else {
                    currentItem?.toDisplayPage(initialUiState.book.totalPageCount) ?: 1
                }
                currentList.clear()
                currentList.addAll(it)
                val newPagerIndex = it.findPagerIndexFor(
                    currentItem = currentItem,
                    targetDisplayPage = currentDisplayPage,
                    totalPageCount = initialUiState.book.totalPageCount,
                )
                if (isFirstLoad || pagerState.currentPage != newPagerIndex) {
                    pagerState.scrollToPage(newPagerIndex)
                }
                if (isFirstLoad) {
                    isInitialized = true
                    updateLastReadPage()
                }
            }
            .launchIn(coroutineScope)
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .onEach {
                if (isInitialized && currentList.isNotEmpty()) {
                    updateLastReadPage()
                }
            }
            .launchIn(coroutineScope)
        bookSettingsFlow
            .flowWithLifecycle(lifecycle)
            .onEach { settings ->
                uiState = uiState.copy(
                    bookSheetUiState = uiState.bookSheetUiState.copy(
                        pageScale = mapPageScale(settings.pageScale),
                    ),
                )
            }
            .launchIn(coroutineScope)
        viewerSettingsFlow
            .flowWithLifecycle(lifecycle)
            .onEach { settings ->
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
            }
            .launchIn(coroutineScope)
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
    }

    private fun mapPageScale(pageScale: BookSettings.PageScale): PageScale = when (pageScale) {
        BookSettings.PageScale.Fit -> PageScale.Fit
        BookSettings.PageScale.FillWidth -> PageScale.FillWidth
        BookSettings.PageScale.FillHeight -> PageScale.FillHeight
        BookSettings.PageScale.Inside -> PageScale.Inside
        BookSettings.PageScale.None -> PageScale.None
        BookSettings.PageScale.FillBounds -> PageScale.FillBounds
    }

    private fun calculateLastReadPage(): Int {
        if (currentList.isEmpty()) {
            return uiState.book.lastPageRead.coerceIn(0, maxOf(0, uiState.book.totalPageCount - 1))
        }
        val currentPage = pagerState.currentPage.coerceIn(0, currentList.lastIndex)
        return currentList[currentPage].toLastReadPage(uiState.book.totalPageCount)
    }

    private fun updateLastReadPage() {
        val lastReadPage = calculateLastReadPage()
        updateLastReadPage(lastReadPage)
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
        updateLastReadPage()
    }

    override fun onStop() {
        updateLastReadPage()
    }

    override fun onPageChange(page: Int) {
        coroutineScope.launch {
            val pagerIndex = currentList.findPagerIndex(page, uiState.book.totalPageCount)
            pagerState.animateScrollToPage(pagerIndex)
        }
    }

    override fun onPageLoad(unratedPage: UnratedPage, bitmap: Bitmap) {
        onPageLoaded(unratedPage, bitmap.imageWidth <= bitmap.imageHeight)
    }
}
