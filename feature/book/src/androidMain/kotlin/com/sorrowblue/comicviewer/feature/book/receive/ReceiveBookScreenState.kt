/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.receive

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import comicviewer.feature.book.generated.resources.Res
import comicviewer.feature.book.generated.resources.book_error_file_not_opened
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal interface ReceiveBookScreenState {
    val uiState: BookScreenUiState
    val currentList: SnapshotStateList<PageItem>
    val pagerState: PagerState
    val systemUiController: SystemUiController

    fun toggleTooltip()

    fun onPageChange(page: Int)

    fun onPageLoaded(unratedPage: UnratedPage, bitmap: coil3.Bitmap)
}

@Composable
internal fun rememberReceiveBookScreenState(
    uri: String?,
    viewModel: ReceiveBookViewModel =
        assistedMetroViewModel<ReceiveBookViewModel, ReceiveBookViewModel.Factory> {
            create(uri)
        },
): ReceiveBookScreenState {
    val appContext = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentList: SnapshotStateList<PageItem> = remember { mutableStateListOf() }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { currentList.size })
    val systemUiController = rememberSystemUiController()
    return remember {
        ReceiveBookScreenStateImpl(
            context = appContext,
            coroutineScope = scope,
            pagerState = pagerState,
            systemUiController = systemUiController,
            currentList = currentList,
            viewerSettingsFlow = viewModel.viewerSettingsFlow,
            bookFlow = viewModel.bookFlow,
            pageItemListFlow = viewModel.pageItemListFlow,
            onPageLoaded = viewModel::onPageLoaded,
        )
    }
}

private class ReceiveBookScreenStateImpl(
    context: Context,
    private val coroutineScope: CoroutineScope,
    override val pagerState: PagerState,
    override val systemUiController: SystemUiController,
    override val currentList: SnapshotStateList<PageItem>,
    viewerSettingsFlow: SharedFlow<ViewerSettings>,
    bookFlow: SharedFlow<BookFile?>,
    pageItemListFlow: Flow<List<PageItem>>,
    private val onPageLoaded: (UnratedPage, Boolean) -> Unit,
) : ReceiveBookScreenState {
    init {
        pageItemListFlow.onEach {
            currentList.clear()
            currentList.addAll(it)
        }.launchIn(coroutineScope)
        bookFlow.onEach { bookFile ->
            if (bookFile != null) {
                uiState = BookScreenUiState.Loaded(
                    bookFile,
                    CollectionId(),
                    BookSheetUiState(bookFile),
                    alwaysOpenFromFirstPage = viewerSettingsFlow.first().alwaysOpenFromFirstPage,
                )
            } else {
                Toast.makeText(
                    context,
                    getString(Res.string.book_error_file_not_opened),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }.launchIn(coroutineScope)
    }

    override var uiState: BookScreenUiState by mutableStateOf(BookScreenUiState.Loading(""))
        private set

    override fun toggleTooltip() {
        if (uiState !is BookScreenUiState.Loaded) return
        uiState =
            (uiState as BookScreenUiState.Loaded).copy(
                isVisibleTooltip = !systemUiController.isSystemBarsVisible,
            )
        systemUiController.isSystemBarsVisible = !systemUiController.isSystemBarsVisible
    }

    override fun onPageChange(page: Int) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    override fun onPageLoaded(unratedPage: UnratedPage, bitmap: Bitmap) {
        onPageLoaded(unratedPage, bitmap.width < bitmap.height)
    }
}
