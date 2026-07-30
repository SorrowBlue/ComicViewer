/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import coil3.Bitmap
import com.sorrowblue.comicviewer.domain.model.file.Book
import kotlinx.coroutines.launch

@Composable
internal fun BookSheet(
    uiState: BookSheetUiState,
    pagerState: PagerState,
    pages: SnapshotStateList<PageItem>,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val scope = rememberCoroutineScope()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = uiState.beyondViewportPageCount,
            pageSize = PageSize.Fill,
            reverseLayout = true,
            key = { pages[it].key },
            modifier = Modifier.fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.DirectionLeft -> {
                                if (pagerState.currentPage > 0) {
                                    scope.launch {
                                        pagerState.scrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                                true
                            }

                            Key.DirectionRight -> {
                                if (pagerState.currentPage < pagerState.pageCount - 1) {
                                    scope.launch {
                                        pagerState.scrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                                true
                            }

                            else -> false
                        }
                    } else {
                        false
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onLongPress = { onLongClick() }) {
                        val x = it.x
                        val w1 = size.width / 3
                        val w2 = w1 * 2
                        if (x.toInt() in 0..<w1) {
                            scope.launch {
                                pagerState.scrollToPage(pagerState.currentPage - 1)
                            }
                        } else if (x.toInt() in w1..<w2) {
                            onClick()
                        } else {
                            scope.launch {
                                pagerState.scrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                }.pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Scroll) {
                                val scrollDelta = event.changes.first().scrollDelta
                                if (scrollDelta.y > 0) {
                                    scope.launch {
                                        pagerState.scrollToPage(pagerState.currentPage + 1)
                                    }
                                } else {
                                    scope.launch {
                                        pagerState.scrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            }
                        }
                    }
                },
        ) { pageIndex ->
            when (val item = pages[pageIndex]) {
                is NextPage -> NextBookSheet(item, onClick = onNextBookClick)

                is BookPage -> BookPage(
                    book = uiState.book,
                    page = item,
                    pageScale = uiState.pageScale,
                    cutWhitespace = uiState.cutWhitespace,
                    onPageLoad = onPageLoad,
                )
            }
        }
    }
}

internal data class BookSheetUiState(
    val book: Book,
    val pageScale: PageScale = PageScale.Fit,
    val cutWhitespace: Boolean = false,
    val beyondViewportPageCount: Int = 3,
)
