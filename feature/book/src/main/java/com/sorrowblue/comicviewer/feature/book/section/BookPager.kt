package com.sorrowblue.comicviewer.feature.book.section

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.BookPageRequest

internal data class BookPagerUiState(
    val book: Book,
    val prevBook: Book?,
    val nextBook: Book?,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookPager2(
    uiState: BookPagerUiState,
    pagerState: PagerState,
    currentList: SnapshotStateList<BookPage>,
    onClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        beyondBoundsPageCount = 2,
        reverseLayout = true,
        modifier = Modifier.fillMaxSize()
    ) { pageIndex ->
        when (val item = currentList[pageIndex]) {
            is BookPage.Next -> {
                if (item.isNext) {
                    NextBookSheet(uiState.nextBook, true, onClick = onNextBookClick)
                } else {
                    NextBookSheet(uiState.prevBook, false, onClick = onNextBookClick)
                }
            }

            is BookPage.Split -> BookSplitPage(currentList, uiState.book, item, onClick)
        }
    }
}

@Composable
fun BookSplitPage(
    currentList: SnapshotStateList<BookPage>,
    book: Book,
    bookPage: BookPage.Split,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ),
        contentAlignment = Alignment.Center
    ) {
        when (bookPage.state) {
            BookPage.Split.State.NOT_LOADED -> {
                AsyncImage(
                    model = BookPageRequest(book to bookPage.index),
                    contentDescription = null,
                    transform = {
                        if (it is AsyncImagePainter.State.Success) {
                            val input = it.result.drawable.toBitmap()
                            if (input.height <= input.width) {
                                // 分割表示する必要あり
                                val index: Int
                                currentList.apply {
                                    index = indexOf(bookPage)
                                    if (0 < index) {
                                        set(
                                            index,
                                            bookPage.copy(state = BookPage.Split.State.LOADED_SPLIT_RIGHT)
                                        )
                                        add(
                                            index + 1,
                                            bookPage.copy(state = BookPage.Split.State.LOADED_SPLIT_LEFT)
                                        )
                                    }
                                }
                                it.copy(
                                    painter = BitmapPainter(
                                        Bitmap.createBitmap(
                                            input,
                                            input.width / 2,
                                            0,
                                            input.width / 2,
                                            input.height
                                        ).asImageBitmap()
                                    )
                                )
                            } else {
                                // 分割表示する必要なし
                                val index: Int
                                currentList.apply {
                                    index = indexOf(bookPage)
                                    if (0 < index) {
                                        set(
                                            index,
                                            bookPage.copy(state = BookPage.Split.State.LOADED_SPLIT_NON)
                                        )
                                    }
                                }
                                it
                            }
                        } else {
                            it
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            BookPage.Split.State.LOADED_SPLIT_NON -> {
                AsyncImage(
                    model = BookPageRequest(book to bookPage.index),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            BookPage.Split.State.LOADED_SPLIT_LEFT -> BookImage(book, bookPage.index, isLeft = true)
            BookPage.Split.State.LOADED_SPLIT_RIGHT -> BookImage(
                book,
                bookPage.index,
                isLeft = false
            )
        }
    }
}

@Composable
fun BookImage(book: Book, index: Int, isLeft: Boolean) {
    AsyncImage(
        model = BookPageRequest(book to index),
        contentDescription = null,
        transform = {
            if (it is AsyncImagePainter.State.Success) {
                it.copy(
                    painter = BitmapPainter(
                        mihirakiSplitTransformation(
                            it.result.drawable.toBitmap(),
                            isLeft
                        ).asImageBitmap()
                    )
                )
            } else {
                it
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

enum class BookViewType {
    NExT,
    SPLIT,
}

sealed interface BookPage {

    val viewType: BookViewType

    data class Next(val isNext: Boolean) : BookPage {

        override val viewType = BookViewType.NExT
    }

    data class Split(val index: Int, val state: State) : BookPage {

        override val viewType = BookViewType.SPLIT

        enum class State {
            NOT_LOADED,
            LOADED_SPLIT_NON,
            LOADED_SPLIT_LEFT,
            LOADED_SPLIT_RIGHT,
        }
    }
}

fun mihirakiSplitTransformation(input: Bitmap, isLeft: Boolean): Bitmap {
    return Bitmap.createBitmap(
        input,
        if (isLeft) 0 else input.width / 2,
        0,
        input.width / 2,
        input.height
    ).apply {
        input.recycle()
    }
}
