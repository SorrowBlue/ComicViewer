package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavBackStackEntry
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteCreateDialogUiState
import kotlinx.coroutines.flow.Flow

interface FavoriteAddScreenState {
    val dialogUiState: FavoriteCreateDialogUiState
    val pagingDataFlow: Flow<PagingData<Favorite>>
    fun onFavoriteClick(favorite: Favorite)
    fun onNewFavoriteClick()
    fun onNameChange(name: String)
    fun onCreateClick()
    fun onDismissRequest()
}

context(NavBackStackEntry)
@Composable
internal fun rememberFavoriteAddScreenState(
    viewModel: FavoriteAddViewModel = hiltViewModel(),
): FavoriteAddScreenState = remember(viewModel) {
    FavoriteAddScreenStateImpl(savedStateHandle = savedStateHandle, viewModel = viewModel)
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
private class FavoriteAddScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    private val viewModel: FavoriteAddViewModel,
) : FavoriteAddScreenState {

    override var dialogUiState by savedStateHandle.saveable {
        mutableStateOf(
            FavoriteCreateDialogUiState()
        )
    }
        private set

    override val pagingDataFlow = viewModel.pagingDataFlow

    override fun onFavoriteClick(favorite: Favorite) {
        viewModel.update(favorite)
    }

    override fun onNewFavoriteClick() {
        dialogUiState =
            dialogUiState.copy(isShown = true)
    }

    override fun onNameChange(name: String) {
        dialogUiState = dialogUiState.copy(
            name = name,
            nameError = name.isBlank()
        )
    }

    override fun onCreateClick() {
        onNameChange(dialogUiState.name)
        if (!dialogUiState.nameError) {
            viewModel.onCreateClick(dialogUiState.name) {
                dialogUiState = FavoriteCreateDialogUiState()
            }
        }
    }

    override fun onDismissRequest() {
        dialogUiState = FavoriteCreateDialogUiState()
    }
}
