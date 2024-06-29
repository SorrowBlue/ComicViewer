package com.sorrowblue.comicviewer.feature.library.dropbox

import android.os.Parcelable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.dropbox.component.DropBoxTopAppBar
import com.sorrowblue.comicviewer.feature.library.dropbox.component.FileListItem
import com.sorrowblue.comicviewer.feature.library.dropbox.data.dropBoxModule
import com.sorrowblue.comicviewer.feature.library.dropbox.navigation.DropBoxGraph
import com.sorrowblue.comicviewer.feature.library.dropbox.section.DropBoxAccountDialog
import com.sorrowblue.comicviewer.feature.library.dropbox.section.DropBoxDialogUiState
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.parcelize.Parcelize
import org.koin.core.context.loadKoinModules

internal interface DropBoxScreenNavigator {
    fun navigateUp()
    fun onFolderClick(folder: Folder)
    fun requireAuthentication()
}

data class DropBoxArgs(val path: String = "")

@Destination<DropBoxGraph>(start = true, navArgs = DropBoxArgs::class)
@Composable
internal fun DropBoxScreen(
    args: DropBoxArgs,
    navBackStackEntry: NavBackStackEntry,
    navigator: DropBoxScreenNavigator,
) {
    loadKoinModules(dropBoxModule)
    DropBoxScreen(
        args = args,
        savedStateHandle = navBackStackEntry.savedStateHandle,
        onBackClick = navigator::navigateUp,
        onFolderClick = navigator::onFolderClick,
        requireAuthentication = navigator::requireAuthentication
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropBoxScreen(
    args: DropBoxArgs,
    savedStateHandle: SavedStateHandle,
    onBackClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    requireAuthentication: () -> Unit,
    state: DropBoxScreenState = rememberDropBoxScreenState(
        args = args,
        savedStateHandle = savedStateHandle
    ),
) {
    state.events.forEach { event ->
        when (event) {
            DropBoxScreenEvent.RequireAuthentication -> {
                state.consumeEvent(event)
                requireAuthentication()
            }
        }
    }

    val uiState = state.uiState
    val lazPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    DropBoxScreen(
        lazyPagingItems = lazPagingItems,
        uiState = uiState,
        onBackClick = onBackClick,
        onProfileImageClick = state::onProfileImageClick,
        onFileClick = { state.onFileClick(it, onFolderClick) },
        onDialogDismissRequest = state::onDialogDismissRequest,
        onLogoutClick = state::onLogoutClick,
    )
    LifecycleEffect(targetEvent = Lifecycle.Event.ON_RESUME, action = state::onResume)
}

@Parcelize
internal data class DropBoxScreenUiState(
    val path: String = "",
    val dropBoxDialogUiState: DropBoxDialogUiState = DropBoxDialogUiState.Hide,
    val profileUri: String = "",
) : Parcelable

@ExperimentalMaterial3Api
@Composable
private fun DropBoxScreen(
    uiState: DropBoxScreenUiState,
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
            DropBoxTopAppBar(
                path = uiState.path,
                profileUri = uiState.profileUri,
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
            ) { index ->
                lazyPagingItems[index]?.let {
                    FileListItem(file = it, onClick = { onFileClick(it) })
                }
            }
        }
    }
    DropBoxAccountDialog(
        uiState = uiState.dropBoxDialogUiState,
        onDismissRequest = onDialogDismissRequest,
        onLogoutClick = onLogoutClick
    )
}
