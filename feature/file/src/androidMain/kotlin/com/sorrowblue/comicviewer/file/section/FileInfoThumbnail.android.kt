package com.sorrowblue.comicviewer.file.section

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

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
