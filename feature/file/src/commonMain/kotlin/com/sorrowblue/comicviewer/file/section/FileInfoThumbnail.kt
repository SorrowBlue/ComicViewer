package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.file.component.FileThumbnailAsyncImage
import com.sorrowblue.comicviewer.file.component.FileThumbnailsCarousel
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@Composable
internal fun FileInfoThumbnail(
    file: File,
    lazyPagingItems: LazyPagingItems<BookThumbnail>?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val navigationSuiteType =
        NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
    Box(modifier = modifier) {
        if (lazyPagingItems != null) {
            val color = if (navigationSuiteType.isNavigationBar) {
                ComicTheme.colorScheme.surfaceVariant
            } else {
                ComicTheme.colorScheme.surfaceContainerHigh
            }
            FileThumbnailsCarousel(
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding,
                modifier = Modifier
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to color.copy(alpha = 1f),
                                0.8f to color.copy(alpha = 0.6f),
                                1f to color.copy(alpha = 0.0f),
                            ),
                            blendMode = BlendMode.DstIn,
                        )
                    },
            )
            Text(
                text = file.name,
                style = ComicTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.BottomStart)
                    .padding(contentPadding)
                    .padding(bottom = 32.dp),
            )
        } else {
            val color = if (navigationSuiteType.isNavigationBar) {
                ComicTheme.colorScheme.surfaceVariant
            } else {
                ComicTheme.colorScheme.surfaceContainerHigh
            }
            FileThumbnailAsyncImage(
                fileThumbnail = FileThumbnail.from(file),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(contentPadding)
                    .clip(RoundedCornerShape(16.dp))
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to color.copy(alpha = 1f),
                                0.8f to color.copy(alpha = 0.6f),
                                1f to color.copy(alpha = 0.0f),
                            ),
                            blendMode = BlendMode.DstIn,
                        )
                    },
            )
            Text(
                text = file.name,
                style = ComicTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.BottomStart)
                    .padding(contentPadding)
                    .padding(bottom = 32.dp),
            )
        }
    }
}

@OptIn(InternalDataApi::class)
@Composable
@Preview(device = "id:pixel_tablet")
private fun FileInfoThumbnailPreview() {
    val lazyPagingItems = PagingData
        .flowData { BookThumbnail.from(fakeBookFile()) }
        .collectAsLazyPagingItems()
    ComicTheme {
        Scaffold {
            FileInfoThumbnail(
                file = BookFile(BookshelfId(), "", "", "", 0, 0, false, "", 0),
                lazyPagingItems = lazyPagingItems,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            )
        }
    }
}

@OptIn(InternalDataApi::class)
@Composable
@Preview
private fun FileInfoThumbnailPreview2() {
    ComicTheme {
        Scaffold {
            FileInfoThumbnail(
                file = BookFile(BookshelfId(), "", "", "", 0, 0, false, "", 0),
                lazyPagingItems = null,
                modifier = Modifier.padding(it),
            )
        }
    }
}
