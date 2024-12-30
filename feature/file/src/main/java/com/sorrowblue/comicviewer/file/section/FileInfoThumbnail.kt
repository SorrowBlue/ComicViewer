package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.file.component.FileThumbnailAsyncImage
import com.sorrowblue.comicviewer.file.component.FileThumbnailsCarousel
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@Composable
internal fun FileInfoThumbnail(
    file: File,
    lazyPagingItems: LazyPagingItems<BookThumbnail>?,
    modifier: Modifier = Modifier,
) {
    val navigationState = LocalNavigationState.current
    Box(modifier = modifier) {
        if (lazyPagingItems != null) {
            FileThumbnailsCarousel(
                lazyPagingItems = lazyPagingItems,
                modifier = Modifier.height(186.dp)
            )
        } else {
            FileThumbnailAsyncImage(
                fileThumbnail = FileThumbnail.from(file),
                modifier = Modifier
                    .height(186.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (navigationState is NavigationState.NavigationBar) {
                            ComicTheme.colorScheme.surfaceVariant
                        } else {
                            ComicTheme.colorScheme.surfaceContainerHigh
                        }
                    )
            )
        }
    }
}

@Composable
@Preview(device = "id:pixel_tablet")
private fun FileInfoThumbnailPreview() {
    val lazyPagingItems = PagingData.flowData(10) { BookThumbnail(BookshelfId(), "", 0, 0, 0) }
        .collectAsLazyPagingItems()

    ComicTheme {
        Scaffold {
            FileInfoThumbnail(
                file = BookFile(BookshelfId(), "", "", "", 0, 0, false, "", 0),
                lazyPagingItems = lazyPagingItems,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
        }
    }
}

@Composable
@Preview
private fun FileInfoThumbnailPreview2() {
    ComicTheme {
        Scaffold {
            FileInfoThumbnail(
                file = BookFile(BookshelfId(), "", "", "", 0, 0, false, "", 0),
                lazyPagingItems = null,
                modifier = Modifier.padding(it)
            )
        }
    }
}
