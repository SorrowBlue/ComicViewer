package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.domain.model.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.ThumbnailImageCache
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeInternalStorage

@Preview
@Composable
private fun BookshelfImageCacheInfoItemPreview() {
    PreviewTheme {
        BookshelfImageCacheInfoItem(
            imageCacheInfo = BookshelfImageCacheInfo(
                fakeInternalStorage(),
                ThumbnailImageCache(50 * 1024 * 1024, 100 * 1024 * 1024),
                BookPageImageCache(50 * 1024 * 1024, 100 * 1024 * 1024)
            ),
            onThumbnailImageCacheClick = {},
            onBookPageImageCacheClick = {},
        )
    }
}

@Preview
@Composable
private fun OtherImageCacheItemPreview() {
    PreviewTheme {
        OtherImageCacheItem(
            imageCache = OtherImageCache(50 * 1024 * 1024, 100 * 1024 * 1024),
            onClick = {}
        )
    }
}
