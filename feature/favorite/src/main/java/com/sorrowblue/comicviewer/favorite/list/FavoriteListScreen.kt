package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.section.FavoriteListAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteListEmptySheet
import com.sorrowblue.comicviewer.favorite.section.FavoriteListSheet
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteCreateDialog
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteCreateDialogUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.SavableState
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.rememberSavableState

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
internal class FavoriteListScreenState(
    override val savedStateHandle: SavedStateHandle,
    private val viewModel: FavoriteListViewModel,
) : SavableState {
    var dialogUiState by savedStateHandle.saveable { mutableStateOf(FavoriteCreateDialogUiState()) }
        private set

    val pagingDataFlow = viewModel.pagingDataFlow

    fun onNameChange(name: String) {
        dialogUiState = dialogUiState.copy(name = name, nameError = name.isBlank())
    }

    fun onDismissRequest() {
        dialogUiState = FavoriteCreateDialogUiState()
    }

    fun onCreateClick() {
        onNameChange(dialogUiState.name)
        if (dialogUiState.nameError) return
        viewModel.create(dialogUiState.name) {
            dialogUiState = FavoriteCreateDialogUiState()
        }
    }

    fun onNewFavoriteClick() {
        dialogUiState = dialogUiState.copy(isShown = true)
    }
}

@Composable
internal fun rememberFavoriteListScreenState(
    viewModel: FavoriteListViewModel = hiltViewModel(),
): FavoriteListScreenState {
    return rememberSavableState(viewModel) { savedStateHandle ->
        FavoriteListScreenState(
            savedStateHandle = savedStateHandle,
            viewModel = viewModel
        )
    }
}

@Composable
internal fun NavBackStackEntry.FavoriteListRoute(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    state: FavoriteListScreenState = rememberFavoriteListScreenState(),
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteListScreen(
        contentPadding = contentPadding,
        lazyPagingItems = lazyPagingItems,
        onSettingsClick = onSettingsClick,
        onFavoriteClick = onFavoriteClick,
        onCreateFavoriteClick = state::onNewFavoriteClick
    )
    val dialogUiState = state.dialogUiState
    FavoriteCreateDialog(
        uiState = dialogUiState,
        onNameChange = state::onNameChange,
        onDismissRequest = state::onDismissRequest,
        onCreateClick = state::onCreateClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteListScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    onCreateFavoriteClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyListState = rememberLazyListState()
    Scaffold(
        topBar = {
            FavoriteListAppBar(
                scrollBehavior = appBarScrollBehavior,
                onSettingsClick = onSettingsClick
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Create Favorite") },
                icon = { Icon(imageVector = ComicIcons.Add, contentDescription = "") },
                onClick = onCreateFavoriteClick
            )
        },
        contentWindowInsets = contentPadding.asWindowInsets(),
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            FavoriteListEmptySheet(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            )
        } else {
            FavoriteListSheet(
                lazyPagingItems = lazyPagingItems,
                contentPadding = innerPadding.add(paddingValues = PaddingValues(bottom = 88.dp)),
                lazyListState = lazyListState,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}
