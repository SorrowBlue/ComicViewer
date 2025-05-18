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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.usecase.file.GetIntentBookUseCase
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import com.sorrowblue.comicviewer.feature.book.section.PageItem
import com.sorrowblue.comicviewer.feature.book.section.UnratedPage
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.SystemUiController
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logcat.logcat
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel

internal interface ReceiveBookScreenState : SaveableScreenState {

    val uiState: BookScreenUiState
    val currentList: SnapshotStateList<PageItem>
    val pagerState: PagerState
    val systemUiController: SystemUiController

    fun toggleTooltip()
    fun onPageChange(page: Int)
    fun onPageLoaded(split: UnratedPage, bitmap: coil3.Bitmap)
}

@KoinViewModel
internal class ReceiveBookViewModel(val getIntentBookUseCase: GetIntentBookUseCase) :
    ViewModel()

@Composable
internal fun rememberReceiveBookScreenState(
    uri: String?,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    currentList: SnapshotStateList<PageItem> = remember { mutableStateListOf() },
    pagerState: PagerState = rememberPagerState(initialPage = 0, pageCount = { currentList.size }),
    systemUiController: SystemUiController = rememberSystemUiController(),
    viewModel: ReceiveBookViewModel = koinViewModel(),
): ReceiveBookScreenState {
    return rememberSaveableScreenState {
        ReceiveBookScreenStateImpl(
            uri = uri,
            context = context,
            scope = scope,
            pagerState = pagerState,
            systemUiController = systemUiController,
            currentList = currentList,
            savedStateHandle = it,
            getIntentBookUseCase = viewModel.getIntentBookUseCase
        )
    }
}

private class ReceiveBookScreenStateImpl(
    uri: String?,
    context: Context,
    private val scope: CoroutineScope,
    override val pagerState: PagerState,
    override val systemUiController: SystemUiController,
    override val currentList: SnapshotStateList<PageItem>,
    override val savedStateHandle: SavedStateHandle,
    private val getIntentBookUseCase: GetIntentBookUseCase,
) : ReceiveBookScreenState {
    init {
        if (uri == null) {
            Toast.makeText(context, "ファイルを開けませんでした", Toast.LENGTH_SHORT).show()
        } else {
            scope.launch {
                getIntentBookUseCase(GetIntentBookUseCase.Request(uri)).collect { resource ->
                    when (resource) {
                        is Resource.Error -> Unit
                        is Resource.Success -> {
                            logcat { "book=${resource.data}" }
                            uiState = BookScreenUiState.Loaded(
                                resource.data,
                                CollectionId(),
                                BookSheetUiState(resource.data)
                            )
                            currentList.clear()
                            currentList.addAll(
                                buildList {
                                    addAll(
                                        (1..resource.data.totalPageCount).map {
                                            BookPage.Default(it - 1)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override var uiState: BookScreenUiState by mutableStateOf(BookScreenUiState.Loading(""))
        private set

    override fun toggleTooltip() {
        if (uiState !is BookScreenUiState.Loaded) return
        uiState =
            (uiState as BookScreenUiState.Loaded).copy(isVisibleTooltip = !systemUiController.isSystemBarsVisible)
        systemUiController.isSystemBarsVisible = !systemUiController.isSystemBarsVisible
    }

    override fun onPageChange(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    val mutex = Mutex()
    override fun onPageLoaded(unratedPage: UnratedPage, bitmap: Bitmap) {
        scope.launch {
            mutex.withLock {
                when (unratedPage) {
                    is BookPage.Spread.Unrated -> {
                        onSpreadPageLoad(unratedPage, bitmap)
                    }

                    is BookPage.Split.Unrated -> onSplitPageLoad(unratedPage, bitmap)
                }
            }
        }
    }

    private fun onSplitPageLoad(split: BookPage.Split.Unrated, bitmap: Bitmap) {
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
    private fun onSpreadPageLoad(spread: BookPage.Spread.Unrated, bitmap: Bitmap) {
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
