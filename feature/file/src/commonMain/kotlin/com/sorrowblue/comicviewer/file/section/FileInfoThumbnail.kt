package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.sorrowblue.comicviewer.framework.ui.adaptive.ExtraPaneScaffoldDefaults
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationBar
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@Composable
internal fun FileInfoThumbnail(
    file: File,
    lazyPagingItems: LazyPagingItems<BookThumbnail>?,
    modifier: Modifier = Modifier,
) {
    val navigationSuiteType =
        NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
    Box(modifier = modifier) {
        if (lazyPagingItems != null) {
            FileThumbnailsCarousel(
                lazyPagingItems = lazyPagingItems,
                contentPadding = PaddingValues(
                    horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding,
                ),
            )
        } else {
            FileThumbnailAsyncImage(
                fileThumbnail = FileThumbnail.from(file),
                modifier = Modifier
                    .height(186.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (navigationSuiteType.isNavigationBar) {
                            ComicTheme.colorScheme.surfaceVariant
                        } else {
                            ComicTheme.colorScheme.surfaceContainerHigh
                        },
                    ),
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
