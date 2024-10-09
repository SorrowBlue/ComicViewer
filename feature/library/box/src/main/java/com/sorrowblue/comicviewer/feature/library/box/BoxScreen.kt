package com.sorrowblue.comicviewer.feature.library.box

import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.box.component.BoxTopAppBar
import com.sorrowblue.comicviewer.feature.library.box.component.FileListItem
import com.sorrowblue.comicviewer.feature.library.box.navigation.BoxGraph
import com.sorrowblue.comicviewer.feature.library.box.section.BoxAccountDialog
import com.sorrowblue.comicviewer.feature.library.box.section.BoxDialogUiState
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.parcelize.Parcelize

interface BoxScreenNavigator {
    fun navigateUp()
    fun onFolderClick(folder: Folder)
    fun requireLogin()
}

data class BoxArgs(val path: String = "")

@Destination<BoxGraph>(start = true, navArgs = BoxArgs::class)
@Composable
internal fun BoxScreen(
    args: BoxArgs,
    navBackStackEntry: NavBackStackEntry,
    navigator2: DestinationsNavigator,
    navigator: BoxScreenNavigator,
) {
    BoxScreen(
        args = args,
        savedStateHandle = navBackStackEntry.savedStateHandle,
        onBackClick = navigator2::navigateUp,
        onFolderClick = navigator::onFolderClick,
        requireLogin = navigator::requireLogin
    )
}

@Composable
private fun BoxScreen(
    args: BoxArgs,
    savedStateHandle: SavedStateHandle,
    onBackClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    requireLogin: () -> Unit,
    state: BoxScreenState = rememberBoxScreenState(
        args = args,
        savedStateHandle = savedStateHandle
    ),
) {
    state.event.forEach {
        when (it) {
            is BoxScreenUiEvent.RequiredAuth -> {
                state.consumeEvent(it)
                requireLogin()
            }
        }
    }
    val uiState = state.uiState
    val lazPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val createFileRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        state::onResult
    )
    BoxScreen(
        lazyPagingItems = lazPagingItems,
        uiState = uiState,
        onBackClick = onBackClick,
        onProfileImageClick = state::onProfileImageClick,
        onFileClick = { state.onClickFile(it, createFileRequest, onFolderClick) },
        onDialogDismissRequest = state::onDialogDismissRequest,
        onLogoutClick = state::onLogoutClick,
    )
}

@Parcelize
internal data class BoxScreenUiState(
    val path: String = "",
    val showDialog: Boolean = false,
    val boxDialogUiState: BoxDialogUiState = BoxDialogUiState(),
    val profileUri: String = "",
    val token: String = "",
) : Parcelable

@Composable
private fun BoxScreen(
    uiState: BoxScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onDialogDismissRequest: () -> Unit,
    onLogoutClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    Scaffold(
        topBar = {
            BoxTopAppBar(
                path = uiState.path,
                profileUri = uiState.profileUri,
                token = uiState.token,
                onBackClick = onBackClick,
                onProfileImageClick = onProfileImageClick,
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        LazyColumn(
            state = lazyListState,
            contentPadding = contentPadding,
            modifier = Modifier.drawVerticalScrollbar(lazyListState)
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.path }
            ) {
                val item = lazyPagingItems[it]
                if (item != null) {
                    FileListItem(
                        file = item,
                        onClick = { onFileClick(item) }
                    )
                }
            }
        }
    }
    if (uiState.showDialog) {
        BoxAccountDialog(
            uiState = uiState.boxDialogUiState,
            onDismissRequest = onDialogDismissRequest,
            onLogoutClick = onLogoutClick
        )
    }
}
