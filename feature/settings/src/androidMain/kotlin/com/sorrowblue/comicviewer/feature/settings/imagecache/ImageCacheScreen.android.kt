package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.domain.model.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ThumbnailImageCache
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeInternalStorage

@Preview
@Composable
private fun ImageCacheScreenPreview() {
    ImageCacheScreen(
        uiState = ThumbnailScreenUiState(
            imageCacheInfos = listOf(
                BookshelfImageCacheInfo(
                    fakeInternalStorage(),
                    ThumbnailImageCache(50 * 1024 * 1024, 100 * 1024 * 1024),
                    BookPageImageCache(50 * 1024 * 1024, 100 * 1024 * 1024)
                ),
            )
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onBackClick = {},
        onClick = { _, _ -> },
    )
}
