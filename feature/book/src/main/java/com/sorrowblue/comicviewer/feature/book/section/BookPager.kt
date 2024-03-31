package com.sorrowblue.comicviewer.feature.book.section

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.trimBorders
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
internal fun BookPage(
    book: Book,
    page: BookPage,
    pageScale: PageScale,
    onPageLoaded: (UnratedPage, Bitmap) -> Unit,
) {
    when (page) {
        is BookPage.Default -> DefaultBookPage(book = book, bookPage = page, pageScale = pageScale)
        is BookPage.Spread -> SpreadBookPage(
            book = book,
            bookPage = page,
            pageScale = pageScale,
            onPageLoaded = onPageLoaded
        )

        is BookPage.Split -> SplitBookPage(
            book = book,
            bookPage = page,
            pageScale = pageScale,
            onPageLoaded = onPageLoaded
        )
    }
}

@Composable
private fun DefaultBookPage(
    book: Book,
    bookPage: BookPage.Default,
    pageScale: PageScale,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    AsyncImage(
        model = BookPageRequest(book to bookPage.index),
        contentScale = pageScale.contentScale,
        contentDescription = null,
        filterQuality = FilterQuality.None,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        transform = {
            when (it) {
                AsyncImagePainter.State.Empty -> it
                is AsyncImagePainter.State.Error -> it
                is AsyncImagePainter.State.Loading -> it
                is AsyncImagePainter.State.Success ->
                    it.copy(
                        result = it.result.copy(
                            it.result.drawable.toBitmap().trimBorders(Color.WHITE)
                                .toDrawable(context.resources)
                        )
                    )
            }
        }
    )
}

@Composable
private fun SplitBookPage(
    book: Book,
    bookPage: BookPage.Split,
    pageScale: PageScale,
    onPageLoaded: (UnratedPage, Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = BookPageRequest(book to bookPage.index),
        contentDescription = null,
        transform = when (bookPage) {
            is BookPage.Split.Unrated -> SpreadSplitTransformation.unrated {
                onPageLoaded(bookPage, it)
            }

            is BookPage.Split.Single -> SpreadSplitTransformation.Single
            is BookPage.Split.Left -> SpreadSplitTransformation.Left
            is BookPage.Split.Right -> SpreadSplitTransformation.Right
        },
        contentScale = pageScale.contentScale,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    )
}

@Composable
private fun SpreadBookPage(
    book: Book,
    bookPage: BookPage.Spread,
    pageScale: PageScale,
    onPageLoaded: (UnratedPage, Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (bookPage is BookPage.Spread.Combine) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .then(modifier)
        ) {
            AsyncImage(
                model = BookPageRequest(book to bookPage.nextIndex),
                contentDescription = null,
                contentScale = pageScale.contentScale,
                error = rememberVectorPainter(ComicIcons.BrokenImage),
                alignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
            )
            AsyncImage(
                model = BookPageRequest(book to bookPage.index),
                contentDescription = null,
                contentScale = pageScale.contentScale,
                error = rememberVectorPainter(ComicIcons.BrokenImage),
                alignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
            )
        }
    } else {
        AsyncImage(
            model = BookPageRequest(book to bookPage.index),
            contentDescription = null,
            contentScale = pageScale.contentScale,
            transform = when (bookPage) {
                is BookPage.Spread.Single -> SpreadCombineTransformation.Single
                is BookPage.Spread.Spread2 -> SpreadCombineTransformation.Spread2
                is BookPage.Spread.Unrated -> SpreadCombineTransformation.unrated {
                    onPageLoaded(bookPage, it)
                }

                else -> SpreadCombineTransformation.Spread2
            },
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
        )
    }
}

object SpreadCombineTransformation {
    fun unrated(change: (Bitmap) -> Unit) = { state: AsyncImagePainter.State ->
        if (state is AsyncImagePainter.State.Success) {
            change(state.result.drawable.toBitmap())
            state
        } else {
            state
        }
    }

    val Single = AsyncImagePainter.DefaultTransform
    val Spread2 = AsyncImagePainter.DefaultTransform
}

object SpreadSplitTransformation {

    fun unrated(
        change: (Bitmap) -> Unit,
    ) = { state: AsyncImagePainter.State ->
        if (state is AsyncImagePainter.State.Success) {
            change(state.result.drawable.toBitmap())
            state
        } else {
            state
        }
    }

    val Single = AsyncImagePainter.DefaultTransform
    val Left = { state: AsyncImagePainter.State ->
        if (state is AsyncImagePainter.State.Success) {
            state.copy(
                painter = BitmapPainter(
                    state.result.drawable.toBitmap().createSplitBitmap(true).asImageBitmap()
                )
            )
        } else {
            state
        }
    }
    val Right = { state: AsyncImagePainter.State ->
        if (state is AsyncImagePainter.State.Success) {
            state.copy(
                painter = BitmapPainter(
                    state.result.drawable.toBitmap().createSplitBitmap(false).asImageBitmap()
                )
            )
        } else {
            state
        }
    }
}

private fun Bitmap.createSplitBitmap(isLeft: Boolean): Bitmap {
    return Bitmap.createBitmap(
        this,
        if (isLeft) 0 else this.width / 2,
        0,
        this.width / 2,
        this.height
    ).apply {
        this@createSplitBitmap.recycle()
    }
}
