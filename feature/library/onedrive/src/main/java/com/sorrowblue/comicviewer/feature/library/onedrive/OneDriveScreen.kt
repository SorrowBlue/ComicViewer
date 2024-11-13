package com.sorrowblue.comicviewer.feature.library.onedrive

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.onedrive.component.FileListItem
import com.sorrowblue.comicviewer.feature.library.onedrive.component.OneDriveTopAppBar
import com.sorrowblue.comicviewer.feature.library.onedrive.navigation.OneDriveGraph
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import java.io.InputStream
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

internal interface OneDriveScreenNavigator {
    fun navigateUp()
    fun onFileClick(file: File)
    fun onProfileImageClick()
}

data class OneDriveArgs(
    val name: String? = null,
    val itemId: String? = null,
)

@Destination<OneDriveGraph>(
    start = true,
    navArgs = OneDriveArgs::class,
    wrappers = [OneDriveScreenWrapper::class],
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun OneDriveScreen(
    args: OneDriveArgs,
    navBackStackEntry: NavBackStackEntry,
    navigator: OneDriveScreenNavigator,
) {
    OneDriveScreen(
        args = args,
        savedStateHandle = navBackStackEntry.savedStateHandle,
        onBackClick = navigator::navigateUp,
        onFolderClick = navigator::onFileClick,
        onProfileImageClick = navigator::onProfileImageClick,
    )
}

@Composable
private fun OneDriveScreen(
    args: OneDriveArgs,
    savedStateHandle: SavedStateHandle,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    state: OneDriveScreenState = rememberOneDriveScreenState(
        args = args,
        savedStateHandle = savedStateHandle
    ),
) {
    val uiState = state.uiState
    val lazPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val createFileRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        state::onResult
    )
    OneDriveScreen(
        lazyPagingItems = lazPagingItems,
        uiState = uiState,
        onBackClick = onBackClick,
        onProfileImageClick = onProfileImageClick,
        onFileClick = { state.onFileClick(it, createFileRequest, onFolderClick) },
    )

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME, onEvent = state::onResume)
}

@Parcelize
internal data class OneDriveScreenUiState(
    val showDialog: Boolean = false,
    val path: String = "",
    @IgnoredOnParcel val profileUri: suspend () -> InputStream? = { null },
) : Parcelable

@Composable
private fun OneDriveScreen(
    uiState: OneDriveScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    onFileClick: (File) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    Scaffold(
        topBar = {
            OneDriveTopAppBar(
                path = uiState.path,
                profileUri = uiState.profileUri,
                onBackClick = onBackClick,
                onProfileImageClick = onProfileImageClick,
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            contentPadding = innerPadding,
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
}
