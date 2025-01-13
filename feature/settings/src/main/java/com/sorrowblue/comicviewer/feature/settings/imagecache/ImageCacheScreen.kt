package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ImageCache
import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.settings.R
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsDetailGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeInternalStorage
import kotlin.math.floor

internal data class ThumbnailScreenUiState(
    val imageCacheInfos: List<BookshelfImageCacheInfo> = emptyList(),
    val otherImageCache: OtherImageCache? = null,
)

@Destination<SettingsDetailGraph>(visibility = CodeGenVisibility.INTERNAL)
@Composable
internal fun ImageCacheScreen(navigator: SettingsDetailNavigator) {
    ImageCacheScreen(onBackClick = navigator::navigateBack)
}

@Composable
private fun ImageCacheScreen(
    onBackClick: () -> Unit,
    state: ImageCacheScreenState = rememberImageCacheScreenState(),
) {
    ImageCacheScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onClick = state::onClick,
    )
}

@Composable
private fun ImageCacheScreen(
    uiState: ThumbnailScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onClick: (BookshelfId, ImageCache) -> Unit,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(id = R.string.settings_label_image_cache)) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBackClick = onBackClick,
    ) {
        uiState.imageCacheInfos.forEach { imageCacheInfo ->
            BookshelfImageCacheInfoItem(
                imageCacheInfo = imageCacheInfo,
                onThumbnailImageCacheClick = {
                    onClick(imageCacheInfo.bookshelf.id, imageCacheInfo.thumbnailImageCache)
                },
                onBookPageImageCacheClick = {
                    onClick(imageCacheInfo.bookshelf.id, imageCacheInfo.bookPageImageCache)
                },
                modifier = Modifier.padding(ComicTheme.dimension.padding)
            )
        }

        uiState.otherImageCache?.let { otherImageCache ->
            OtherImageCacheItem(
                imageCache = otherImageCache,
                onClick = { onClick(BookshelfId(), otherImageCache) }
            )
        }
    }
}

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

val Long.megaByte get() = floor(this / 1024.0 / 1024.0 * 100.0) / 100.0
