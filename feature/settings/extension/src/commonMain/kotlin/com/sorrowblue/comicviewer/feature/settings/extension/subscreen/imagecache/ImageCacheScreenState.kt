/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ImageCache
import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun rememberImageCacheScreenState(
    viewModel: ImageCacheViewModel = metroViewModel(),
): ImageCacheScreenState {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    return remember(scope, snackbarHostState) {
        ImageCacheScreenStateImpl(
            coroutineScope = scope,
            snackbarHostState = snackbarHostState,
            eventFlow = viewModel.eventFlow,
            bookshelfImageCacheInfoFlow = viewModel.bookshelfImageCacheInfoFlow,
            otherImageCacheInfoFlow = viewModel.otherImageCacheInfoFlow,
            clearImageCache = viewModel::clearImageCache,
        )
    }
}

internal interface ImageCacheScreenState {
    val uiState: ThumbnailScreenUiState
    val snackbarHostState: SnackbarHostState
    fun onClick(bookshelfId: BookshelfId, imageCache: ImageCache)
}

private class ImageCacheScreenStateImpl(
    coroutineScope: CoroutineScope,
    override val snackbarHostState: SnackbarHostState,
    eventFlow: SharedFlow<ImageCacheViewModelEvent>,
    bookshelfImageCacheInfoFlow: SharedFlow<List<BookshelfImageCacheInfo>>,
    otherImageCacheInfoFlow: SharedFlow<OtherImageCache>,
    private val clearImageCache: (BookshelfId, ImageCache) -> Unit,
) : ImageCacheScreenState {

    override var uiState: ThumbnailScreenUiState by mutableStateOf(ThumbnailScreenUiState())
        private set

    init {
        combine(
            bookshelfImageCacheInfoFlow,
            otherImageCacheInfoFlow,
        ) { bookshelfImageCacheInfos, otherImageCache ->
            uiState = ThumbnailScreenUiState(
                imageCacheInfos = bookshelfImageCacheInfos,
                otherImageCache = otherImageCache,
            )
        }.launchIn(coroutineScope)
        eventFlow.onEach {
            when (it) {
                is ImageCacheViewModelEvent.CompleteClearImageCache -> {
                    snackbarHostState.showSnackbar("画像キャッシュを削除しました。")
                }
            }
        }.launchIn(coroutineScope)
    }

    override fun onClick(bookshelfId: BookshelfId, imageCache: ImageCache) {
        clearImageCache(bookshelfId, imageCache)
    }
}
