package com.sorrowblue.comicviewer.feature.book.section

import android.graphics.Bitmap
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.file.Book

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
    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 3,
        pageSize = PageSize.Fill,
        reverseLayout = true,
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick,
                onLongClick = onLongClick
            ),
        key = { pages[it].key }
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
