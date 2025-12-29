package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil3.Bitmap
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.framework.ui.preview.layout.scratch
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
    val scope = rememberCoroutineScope()
    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 3,
        pageSize = PageSize.Fill,
        reverseLayout = true,
        key = { pages[it].key },
        modifier = modifier.fillMaxSize(),
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
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() } ,
                    indication = null,
                    onLongClick = onLongClick,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(pagerState.currentPage - 1)
                        }
                    }
                )
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() } ,
                    indication = null,
                    onLongClick = onLongClick,
                    onClick = onClick
                )
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() } ,
                    indication = null,
                    onLongClick = onLongClick,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(pagerState.currentPage + 1)
                        }
                    }
                )
        )
    }
}

internal data class BookSheetUiState(val book: Book, val pageScale: PageScale = PageScale.Fit)
