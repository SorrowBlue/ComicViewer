package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal sealed interface FavoriteEditScreenStateEvent {
    data object EditComplete : FavoriteEditScreenStateEvent
}

internal interface FavoriteEditScreenState : SaveableScreenState {
    val uiState: FavoriteEditScreenUiState
    val events: EventFlow<FavoriteEditScreenStateEvent>
    val pagingDataFlow: Flow<PagingData<File>>
    fun onDeleteClick(file: File)
    fun onSaveClick(text: String)
}

@Composable
internal fun rememberFavoriteEditScreenState(
    route: FavoriteEdit,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteEditViewModel = koinViewModel { parametersOf(route) },
): FavoriteEditScreenState = rememberSaveableScreenState {
    FavoriteEditScreenStateImpl(
        savedStateHandle = it,
        scope = scope,
        args = route,
        getFavoriteUseCase = viewModel.getFavoriteUseCase,
        updateFavoriteUseCase = viewModel.updateFavoriteUseCase,
        removeFavoriteFileUseCase = viewModel.removeFavoriteFileUseCase,
        pagingDataFlow = viewModel.pagingDataFlow
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
private class FavoriteEditScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    private val scope: CoroutineScope,
    private val args: FavoriteEdit,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
    override val pagingDataFlow: Flow<PagingData<File>>,
) : FavoriteEditScreenState {

    override var uiState by savedStateHandle.saveable { mutableStateOf(FavoriteEditScreenUiState()) }
        private set

    override val events = EventFlow<FavoriteEditScreenStateEvent>()

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
            updateFavoriteUseCase(UpdateFavoriteUseCase.Request(favorite.copy(name = text)))
                .collect()
            events.tryEmit(FavoriteEditScreenStateEvent.EditComplete)
        }
    }
}
