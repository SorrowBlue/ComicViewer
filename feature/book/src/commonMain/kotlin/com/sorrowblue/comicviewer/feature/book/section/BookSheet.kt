package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import coil3.Bitmap
import com.sorrowblue.comicviewer.domain.model.file.Book
import kotlinx.coroutines.launch
import logcat.logcat

internal data class BookSheetUiState(
    val book: Book,
    val pageScale: PageScale = PageScale.Fit,
)

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
    val scope = rememberCoroutineScope()
    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 3,
        pageSize = PageSize.Fill,
        reverseLayout = true,
        key = { pages[it].key },
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongClick() }) {
                    logcat { "offset $it" }
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
            }
    ) { pageIndex ->
        when (val item = pages[pageIndex]) {
            is NextPage -> NextBookSheet(item, onClick = onNextBookClick)
            is BookPage -> BookPage(
                book = uiState.book,
                page = item,
                pageScale = uiState.pageScale,
                onPageLoad = onPageLoad,
            )
        }
    }
}
