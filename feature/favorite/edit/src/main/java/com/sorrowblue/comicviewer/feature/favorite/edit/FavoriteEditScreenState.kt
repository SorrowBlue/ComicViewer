package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavBackStackEntry
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.FavoriteEditArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface FavoriteEditScreenState {
    val uiState: FavoriteEditScreenUiState
    val pagingDataFlow: Flow<PagingData<File>>
    fun onDeleteClick(file: File)
    fun onNameChange(name: String)
    fun onSaveClick(onComplete: () -> Unit)
}

context(NavBackStackEntry)
@Composable
internal fun rememberFavoriteEditScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteEditViewModel = hiltViewModel(),
): FavoriteEditScreenState = remember {
    FavoriteEditScreenStateImpl(
        args = FavoriteEditArgs(arguments!!),
        scope = scope,
        savedStateHandle = savedStateHandle,
        viewModel = viewModel
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
private class FavoriteEditScreenStateImpl(
    scope: CoroutineScope,
    savedStateHandle: SavedStateHandle,
    private val args: FavoriteEditArgs,
    private val viewModel: FavoriteEditViewModel,
) : FavoriteEditScreenState {

    override var uiState by savedStateHandle.saveable { mutableStateOf(FavoriteEditScreenUiState()) }
        private set

    override val pagingDataFlow = viewModel.pagingDataFlow(args.favoriteId)

    init {
        if (uiState.name.isBlank()) {
            scope.launch {
                viewModel.getFavoriteUseCase.execute(GetFavoriteUseCase.Request(args.favoriteId))
                    .first().dataOrNull?.let {
                        uiState = uiState.copy(name = it.name)
                    }
            }
        }
    }

    override fun onDeleteClick(file: File) {
        viewModel.removeFile(args.favoriteId, file)
    }

    override fun onNameChange(name: String) {
        uiState = uiState.copy(name = name, nameError = name.isBlank())
    }

    override fun onSaveClick(onComplete: () -> Unit) {
        onNameChange(uiState.name)
        if (uiState.nameError) return
        viewModel.save(args.favoriteId, uiState.name, onComplete)
    }
}
