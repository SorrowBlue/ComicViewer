package com.sorrowblue.comicviewer.feature.book.section

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import com.sorrowblue.comicviewer.domain.model.file.Book

internal data class BookSheetUiState(
    val book: Book,
    val pageScale: PageScale = PageScale.Fit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookSheet(
    uiState: BookSheetUiState,
    pagerState: PagerState,
    pages: List<BookItem>,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    onPageLoaded: (UnratedPage, Bitmap) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 4,
        pageSize = object : PageSize {
            override fun Density.calculateMainAxisPageSize(
                availableSpace: Int,
                pageSpacing: Int,
            ): Int {
                return availableSpace
            }
        },
        reverseLayout = true,
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        key = {
            when (val item = pages[it]) {
                is NextBook -> item.key
                is BookPage -> item.key
            }
        }
    ) { pageIndex ->
        when (val item = pages[pageIndex]) {
            is NextBook -> NextBookSheet(item, onClick = onNextBookClick)
            is BookPage -> BookPage(
                book = uiState.book,
                page = item,
                pageScale = uiState.pageScale,
                onPageLoaded = onPageLoaded,
            )
        }
    }
}
