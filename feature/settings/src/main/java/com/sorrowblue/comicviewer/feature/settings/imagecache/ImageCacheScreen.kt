package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.FavoriteImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.feature.settings.R
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsDetailGraph
import kotlin.math.floor

internal data class ThumbnailScreenUiState(
    val imageCacheInfos: List<ImageCacheInfo> = emptyList(),
)

@Destination<SettingsDetailGraph>(visibility = CodeGenVisibility.INTERNAL)
@Composable
internal fun ImageCacheScreen(
    contentPadding: PaddingValues,
    navigator: SettingsDetailNavigator,
) {
    ImageCacheScreen(
        onBackClick = navigator::navigateBack,
        contentPadding = contentPadding
    )
}

@Composable
private fun ImageCacheScreen(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    state: ImageCacheScreenState = rememberImageCacheScreenState(),
) {
    ImageCacheScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onClick = state::onClick,
        contentPadding = contentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageCacheScreen(
    uiState: ThumbnailScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onClick: (ImageCacheInfo) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(id = R.string.settings_label_image_cache)) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBackClick = onBackClick,
        contentPadding = contentPadding,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            uiState.imageCacheInfos.forEach { imageCacheInfo ->
                when (imageCacheInfo) {
                    is BookshelfImageCacheInfo ->
                        BookshelfImageCacheInfoItem(
                            imageCacheInfo = imageCacheInfo,
                            onClick = { onClick(imageCacheInfo) }
                        )

                    is FavoriteImageCacheInfo -> {}
                }
            }
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
                    InternalStorage("aaaaaaaaa"),
                    BookshelfImageCacheInfo.Type.Thumbnail,
                    50,
                    100
                ),
                BookshelfImageCacheInfo(
                    InternalStorage("bbbbbbbb"),
                    BookshelfImageCacheInfo.Type.Page,
                    50,
                    100
                ),
                FavoriteImageCacheInfo(
                    33,
                    250
                ),
            )
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onBackClick = {},
        onClick = {},
        contentPadding = PaddingValues()
    )
}

val Long.megaByte get() = floor(this / 1024.0 / 1024.0 * 100.0) / 100.0
