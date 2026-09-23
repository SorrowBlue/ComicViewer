/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.Bitmap
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Size
import coil3.toBitmap
import coil3.transform.Transformation
import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookPageImage
import com.sorrowblue.comicviewer.feature.book.asImageBitmap
import com.sorrowblue.comicviewer.feature.book.createSplitBitmap
import com.sorrowblue.comicviewer.feature.book.trimBorders
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.book.generated.resources.Res
import comicviewer.feature.book.generated.resources.book_action_reload
import comicviewer.feature.book.generated.resources.book_msg_page_not_loaded
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookPage(
    book: Book,
    page: BookPage,
    pageScale: PageScale,
    cutWhitespace: Boolean,
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
) {
    when (page) {
        is BookPage.Default -> DefaultBookPage(
            book = book,
            bookPage = page,
            pageScale = pageScale,
            cutWhitespace = cutWhitespace,
        )

        is BookPage.Spread -> SpreadBookPage(
            book = book,
            bookPage = page,
            pageScale = pageScale,
            onPageLoad = onPageLoad,
        )

        is BookPage.Split -> SplitBookPage(
            book = book,
            bookPage = page,
            pageScale = pageScale,
            onPageLoad = onPageLoad,
        )
    }
}

object WhiteTrimTransformation : Transformation() {
    override val cacheKey = "${this::class.qualifiedName}"
    override suspend fun transform(input: Bitmap, size: Size): Bitmap =
        input.trimBorders(Color.White)
}

@Composable
private fun DefaultBookPage(
    book: Book,
    bookPage: BookPage.Default,
    pageScale: PageScale,
    cutWhitespace: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalPlatformContext.current
    val request = remember(bookPage.index, cutWhitespace) {
        ImageRequest
            .Builder(context)
            .data(BookPageImage(book to bookPage.index))
            .apply {
                if (cutWhitespace) {
                    transformations(WhiteTrimTransformation)
                }
            }
            .build()
    }
    val painter = rememberAsyncImagePainter(
        model = request,
        contentScale = pageScale.contentScale,
        filterQuality = FilterQuality.None,
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = pageScale.contentScale,
            modifier = Modifier.fillMaxSize(),
        )
        val state by painter.state.collectAsStateWithLifecycle()
        when (state) {
            is AsyncImagePainter.State.Error -> {
                PageErrorContent(onReload = { painter.restart() })
            }

            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .wrapContentSize()
                        .padding(ComicTheme.dimension.margin),
                )
            }

            AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Success -> Unit
        }
    }
}

@Composable
private fun SplitBookPage(
    book: Book,
    bookPage: BookPage.Split,
    pageScale: PageScale,
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalPlatformContext.current
    val request = remember(bookPage.index) {
        ImageRequest
            .Builder(context)
            .data(BookPageImage(book to bookPage.index))
            .build()
    }
    val painter = rememberAsyncImagePainter(
        model = request,
        contentScale = pageScale.contentScale,
        filterQuality = FilterQuality.None,
    )
    val state by painter.state.collectAsStateWithLifecycle()
    val currentOnPageLoad by rememberUpdatedState(onPageLoad)

    if (bookPage is BookPage.Split.Unrated && state is AsyncImagePainter.State.Success) {
        key(state) {
            SideEffect {
                val bitmap = (state as AsyncImagePainter.State.Success).result.image.toBitmap()
                currentOnPageLoad(bookPage, bitmap)
            }
        }
    }

    val splitBitmap = remember(state, bookPage) {
        if (state is AsyncImagePainter.State.Success) {
            val original = (state as AsyncImagePainter.State.Success).result.image.toBitmap()
            when (bookPage) {
                is BookPage.Split.Left -> original.createSplitBitmap(isLeft = true)
                is BookPage.Split.Right -> original.createSplitBitmap(isLeft = false)
                else -> null
            }
        } else {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) {
        when {
            splitBitmap != null -> {
                Image(
                    bitmap = splitBitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = pageScale.contentScale,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            bookPage !is BookPage.Split.Left && bookPage !is BookPage.Split.Right -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = pageScale.contentScale,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        when (state) {
            is AsyncImagePainter.State.Error -> {
                PageErrorContent(onReload = { painter.restart() })
            }

            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .wrapContentSize()
                        .padding(ComicTheme.dimension.margin),
                )
            }

            AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Success -> Unit
        }
    }
}

@Composable
private fun SpreadBookPage(
    book: Book,
    bookPage: BookPage.Spread,
    pageScale: PageScale,
    onPageLoad: (UnratedPage, Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (bookPage is BookPage.Spread.Combine) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .then(modifier),
        ) {
            AsyncImage(
                model = BookPageImage(book to bookPage.nextIndex),
                contentDescription = null,
                contentScale = pageScale.contentScale,
                error = rememberVectorPainter(ComicIcons.BrokenImage),
                alignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
            )
            AsyncImage(
                model = BookPageImage(book to bookPage.index),
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
        val context = LocalPlatformContext.current
        val request = remember(bookPage.index) {
            ImageRequest
                .Builder(context)
                .data(BookPageImage(book to bookPage.index))
                .build()
        }
        val painter = rememberAsyncImagePainter(
            model = request,
            contentScale = pageScale.contentScale,
            filterQuality = FilterQuality.None,
        )
        val state by painter.state.collectAsStateWithLifecycle()
        val currentOnPageLoad by rememberUpdatedState(onPageLoad)

        if (bookPage is BookPage.Spread.Unrated && state is AsyncImagePainter.State.Success) {
            key(state) {
                SideEffect {
                    val bitmap = (state as AsyncImagePainter.State.Success).result.image.toBitmap()
                    currentOnPageLoad(bookPage, bitmap)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = pageScale.contentScale,
                modifier = Modifier.fillMaxSize(),
            )

            when (state) {
                is AsyncImagePainter.State.Error -> {
                    PageErrorContent(onReload = { painter.restart() })
                }

                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .wrapContentSize()
                            .padding(ComicTheme.dimension.margin),
                    )
                }

                AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Success -> Unit
            }
        }
    }
}

@Composable
private fun PageErrorContent(onReload: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) {
        Icon(
            modifier = Modifier.size(96.dp),
            painter = rememberVectorPainter(image = ComicIcons.BrokenImage),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
        Text(
            text = stringResource(Res.string.book_msg_page_not_loaded),
            style = ComicTheme.typography.bodyLarge,
        )

        OutlinedButton(onClick = onReload) {
            Text(text = stringResource(Res.string.book_action_reload))
        }
    }
}
