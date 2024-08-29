package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.ImageCache
import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetBookshelfImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetOtherImageCacheInfoUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
internal fun rememberImageCacheScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ImageCacheScreenViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): ImageCacheScreenState = remember {
    ImageCacheScreenStateImpl(
        scope = scope,
        snackbarHostState = snackbarHostState,
        getBookshelfImageCacheInfoUseCase = viewModel.getBookshelfImageCacheInfoUseCase,
        getOtherImageCacheInfoUseCase = viewModel.getOtherImageCacheInfoUseCase,
        clearImageCacheUseCase = viewModel.clearImageCacheUseCase
    )
}

internal interface ImageCacheScreenState {
    fun onClick(bookshelfId: BookshelfId, imageCache: ImageCache)
    val snackbarHostState: SnackbarHostState
    val uiState: ThumbnailScreenUiState
}

private class ImageCacheScreenStateImpl(
    override val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
    private val getBookshelfImageCacheInfoUseCase: GetBookshelfImageCacheInfoUseCase,
    private val getOtherImageCacheInfoUseCase: GetOtherImageCacheInfoUseCase,
    private val clearImageCacheUseCase: ClearImageCacheUseCase,
) : ImageCacheScreenState {
    override fun onClick(bookshelfId: BookshelfId, imageCache: ImageCache) {
        val request = when (imageCache) {
            is ThumbnailImageCache ->
                ClearImageCacheUseCase.BookshelfRequest(bookshelfId, imageCache)

            is BookPageImageCache ->
                ClearImageCacheUseCase.BookshelfRequest(bookshelfId, imageCache)

            is OtherImageCache -> ClearImageCacheUseCase.OtherRequest
        }
        scope.launch {
            clearImageCacheUseCase(request)
            fetch()
            snackbarHostState.showSnackbar("画像キャッシュを削除しました。")
        }
    }

    override var uiState: ThumbnailScreenUiState by mutableStateOf(ThumbnailScreenUiState())
        private set

    init {
        fetch()
    }

    private fun fetch() {
        scope.launch {
            getBookshelfImageCacheInfoUseCase(GetBookshelfImageCacheInfoUseCase.Request).first()
                .onSuccess {
                    uiState = uiState.copy(imageCacheInfos = it)
                }
            getOtherImageCacheInfoUseCase(GetOtherImageCacheInfoUseCase.Request).onSuccess {
                uiState = uiState.copy(otherImageCache = it)
            }
        }
    }
}
