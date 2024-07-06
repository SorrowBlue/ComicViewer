package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.FavoriteImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetImageCacheInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
        getImageCacheInfoUseCase = viewModel.getImageCacheInfoUseCase,
        clearImageCacheUseCase = viewModel.clearImageCacheUseCase
    )
}

internal interface ImageCacheScreenState {
    fun onClick(imageCacheInfo: ImageCacheInfo)
    val snackbarHostState: SnackbarHostState
    val uiState: ThumbnailScreenUiState
}

private class ImageCacheScreenStateImpl(
    override val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
    private val getImageCacheInfoUseCase: GetImageCacheInfoUseCase,
    private val clearImageCacheUseCase: ClearImageCacheUseCase,
) : ImageCacheScreenState {
    override fun onClick(imageCacheInfo: ImageCacheInfo) {
        val request = when (imageCacheInfo) {
            is BookshelfImageCacheInfo -> ClearImageCacheUseCase.BookshelfRequest(
                imageCacheInfo.bookshelf.id,
                imageCacheInfo.type
            )

            is FavoriteImageCacheInfo -> ClearImageCacheUseCase.FavoriteRequest
        }
        scope.launch {
            clearImageCacheUseCase.execute(request).first()
            getImageCacheInfoUseCase.execute(GetImageCacheInfoUseCase.Request).first().onSuccess {
                uiState = uiState.copy(imageCacheInfos = it)
            }
            snackbarHostState.showSnackbar("画像キャッシュを削除しました。")
        }
    }

    override var uiState: ThumbnailScreenUiState by mutableStateOf(ThumbnailScreenUiState())
        private set

    init {
        scope.launch {
            getImageCacheInfoUseCase.execute(GetImageCacheInfoUseCase.Request).first().onSuccess {
                uiState = uiState.copy(imageCacheInfos = it)
            }
        }
    }
}

@HiltViewModel
internal class ImageCacheScreenViewModel @Inject constructor(
    val getImageCacheInfoUseCase: GetImageCacheInfoUseCase,
    val clearImageCacheUseCase: ClearImageCacheUseCase,
) : ViewModel()
