package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal sealed interface FavoriteEditScreenStateEvent {
    data object EditComplete : FavoriteEditScreenStateEvent
}

internal interface FavoriteEditScreenState :
    SaveableScreenState,
    ScreenStateEvent<FavoriteEditScreenStateEvent> {
    val uiState: FavoriteEditScreenUiState
    val pagingDataFlow: Flow<PagingData<File>>
    fun onDeleteClick(file: File)
    fun onSaveClick(text: String)
}

@Composable
internal fun rememberFavoriteEditScreenState(
    args: FavoriteEditArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteEditViewModel = hiltViewModel(),
): FavoriteEditScreenState = rememberSaveableScreenState {
    FavoriteEditScreenStateImpl(
        viewModel = viewModel,
        savedStateHandle = it,
        scope = scope,
        args = args,
        getFavoriteUseCase = viewModel.getFavoriteUseCase,
        updateFavoriteUseCase = viewModel.updateFavoriteUseCase,
        removeFavoriteFileUseCase = viewModel.removeFavoriteFileUseCase
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
private class FavoriteEditScreenStateImpl(
    viewModel: FavoriteEditViewModel,
    override val savedStateHandle: SavedStateHandle,
    override val scope: CoroutineScope,
    private val args: FavoriteEditArgs,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
) : FavoriteEditScreenState {

    override val event = MutableSharedFlow<FavoriteEditScreenStateEvent>()

    override var uiState by savedStateHandle.saveable { mutableStateOf(FavoriteEditScreenUiState()) }
        private set

    override val pagingDataFlow = viewModel.pagingDataFlow

    init {
        if (uiState.initName.isBlank()) {
            scope.launch {
                getFavoriteUseCase(GetFavoriteUseCase.Request(args.favoriteId))
                    .first().dataOrNull()?.let {
                        uiState = uiState.copy(initName = it.name)
                    }
            }
        }
    }

    override fun onDeleteClick(file: File) {
        val request =
            RemoveFavoriteFileUseCase.Request(
                FavoriteFile(
                    args.favoriteId,
                    file.bookshelfId,
                    file.path
                )
            )
        scope.launch {
            removeFavoriteFileUseCase(request).collect()
        }
    }

    override fun onSaveClick(text: String) {
        scope.launch {
            val favorite = getFavoriteUseCase(GetFavoriteUseCase.Request(args.favoriteId))
                .first().dataOrNull() ?: return@launch
            updateFavoriteUseCase.execute(UpdateFavoriteUseCase.Request(favorite.copy(name = text)))
                .collect()
            sendEvent(FavoriteEditScreenStateEvent.EditComplete)
        }
    }
}
