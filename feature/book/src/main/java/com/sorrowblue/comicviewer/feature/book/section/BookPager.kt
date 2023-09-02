package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.sorrowblue.comicviewer.feature.book.trimBorders
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.request.BookPageRequest

internal data class BookPagerUiState(
    val book: Book,
    val prevBook: Book?,
    val nextBook: Book?
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookPager(
    pagerState: PagerState,
    uiState: BookPagerUiState,
    onClick: () -> Unit,
    onNextBookClick: (Book) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 2,
        reverseLayout = true,
        modifier = Modifier.fillMaxSize()
    ) { pageIndex ->
        when (pageIndex) {
            0 ->
                NextBookSheet(uiState.prevBook, false, onClick = onNextBookClick)

            uiState.book.totalPageCount + 1 ->
                NextBookSheet(uiState.nextBook, true, onClick = onNextBookClick)

            else ->
                Box(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    var state by remember {
                        mutableStateOf<AsyncImagePainter.State>(
                            AsyncImagePainter.State.Empty
                        )
                    }
                    AsyncImage(
                        model = BookPageRequest(uiState.book to pageIndex - 1),
                        null,
                        onState = { state = it },
                        transform = {
                            when (it) {
                                AsyncImagePainter.State.Empty -> it
                                is AsyncImagePainter.State.Error -> it
                                is AsyncImagePainter.State.Loading -> it
                                is AsyncImagePainter.State.Success -> {
                                    it.copy(
                                        painter = BitmapPainter(
                                            it.result.drawable.toBitmap().trimBorders(
                                                android.graphics.Color.WHITE
                                            ).asImageBitmap()
                                        ),
                                    )
                                }
                            }
                        },
                    modifier = Modifier.fillMaxSize()
                )
                if (state is AsyncImagePainter.State.Loading) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
