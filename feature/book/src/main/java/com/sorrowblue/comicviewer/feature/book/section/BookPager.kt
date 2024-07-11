package com.sorrowblue.comicviewer.feature.book.section

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil3.annotation.ExperimentalCoilApi
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.toBitmap
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.R
import com.sorrowblue.comicviewer.feature.book.WhiteTrimTransformation
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun BookPage(
    book: Book,
    page: BookPage,
    pageScale: PageScale,
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
) {
    when (page) {
        is BookPage.Default -> DefaultBookPage(book = book, bookPage = page, pageScale = pageScale)
        is BookPage.Spread -> SpreadBookPage(
            book = book,
            bookPage = page,
            pageScale = pageScale,
            onPageLoad = onPageLoad
        )

        is BookPage.Split -> SplitBookPage(
            book = book,
            bookPage = page,
            pageScale = pageScale,
            onPageLoad = onPageLoad
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
    val request = ImageRequest.Builder(context)
        .data(BookPageRequest(book to bookPage.index))
        .transformations(WhiteTrimTransformation)
        .build()
    SubcomposeAsyncImage(
        model = request,
        contentDescription = null,
        contentScale = pageScale.contentScale,
        filterQuality = FilterQuality.None,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        loading = {
            CircularProgressIndicator(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.TopStart)
                    .padding(ComicTheme.dimension.margin)
            )
            Spacer(modifier = Modifier.fillMaxSize())
        },
        error = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(96.dp),
                    painter = rememberVectorPainter(image = ComicIcons.BrokenImage),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
                Text(
                    text = stringResource(R.string.book_msg_page_not_loaded),
                    style = ComicTheme.typography.bodyLarge
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
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = BookPageRequest(book to bookPage.index),
        contentDescription = null,
        transform = when (bookPage) {
            is BookPage.Split.Unrated -> SpreadSplitTransformation.unrated {
                onPageLoad(bookPage, it)
            }

            is BookPage.Split.Single -> SpreadSplitTransformation.Single
            is BookPage.Split.Left -> SpreadSplitTransformation.left()
            is BookPage.Split.Right -> SpreadSplitTransformation.right()
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
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
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
                is BookPage.Spread.Unrated -> SpreadCombineTransformation.unrated(context) {
                    onPageLoad(bookPage, it)
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
    @OptIn(ExperimentalCoilApi::class)
    fun unrated(context: Context, change: (Bitmap) -> Unit) = { state: AsyncImagePainter.State ->
        if (state is AsyncImagePainter.State.Success) {
            change(state.result.image.asDrawable(context.resources).toBitmap())
            state
        } else {
            state
        }
    }

    val Single = AsyncImagePainter.DefaultTransform
    val Spread2 = AsyncImagePainter.DefaultTransform
}

@OptIn(ExperimentalCoilApi::class)
object SpreadSplitTransformation {

    fun unrated(change: (Bitmap) -> Unit) = { state: AsyncImagePainter.State ->
        if (state is AsyncImagePainter.State.Success) {
            change(state.result.image.toBitmap())
            state
        } else {
            state
        }
    }

    val Single = AsyncImagePainter.DefaultTransform

    fun left() = { state: AsyncImagePainter.State ->
        if (state is AsyncImagePainter.State.Success) {
            state.copy(
                painter = BitmapPainter(
                    state.result.image.toBitmap()
                        .createSplitBitmap(true).asImageBitmap()
                )
            )
        } else {
            state
        }
    }

    fun right() = { state: AsyncImagePainter.State ->
        if (state is AsyncImagePainter.State.Success) {
            state.copy(
                painter = BitmapPainter(
                    state.result.image.toBitmap()
                        .createSplitBitmap(false).asImageBitmap()
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
