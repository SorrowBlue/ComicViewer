package com.sorrowblue.comicviewer.feature.library.googledrive

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
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.googledrive.component.FileListItem
import com.sorrowblue.comicviewer.feature.library.googledrive.component.GoogleDriveTopAppBar
import com.sorrowblue.comicviewer.feature.library.googledrive.data.googleAuthModule
import com.sorrowblue.comicviewer.feature.library.googledrive.data.googleDriveModule
import com.sorrowblue.comicviewer.feature.library.googledrive.navigation.GoogleDriveGraph
import com.sorrowblue.comicviewer.feature.library.googledrive.section.GoogleAccountDialog
import com.sorrowblue.comicviewer.feature.library.googledrive.section.GoogleAccountDialogUiState
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.parcelize.Parcelize
import org.koin.core.context.loadKoinModules

interface GoogleDriveScreenNavigator {
    fun navigateUp()
    fun onFolderClick(folder: Folder)
    fun requireAuthentication()
}

data class GoogleDriveArgs(val path: String = "root")

@Destination<GoogleDriveGraph>(start = true, navArgs = GoogleDriveArgs::class)
@Composable
internal fun GoogleDriveScreen(
    args: GoogleDriveArgs,
    navBackStackEntry: NavBackStackEntry,
    navigator: GoogleDriveScreenNavigator,
) {
    loadKoinModules(listOf(googleAuthModule, googleDriveModule))
    GoogleDriveScreen(
        args = args,
        savedStateHandle = navBackStackEntry.savedStateHandle,
        onBackClick = navigator::navigateUp,
        onFolderClick = navigator::onFolderClick,
        requireAuthentication = navigator::requireAuthentication
    )
}

@Composable
private fun GoogleDriveScreen(
    args: GoogleDriveArgs,
    savedStateHandle: SavedStateHandle,
    onBackClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    requireAuthentication: () -> Unit,
    state: GoogleDriveScreenState = rememberGoogleDriveScreenState(args = args, savedStateHandle),
) {
    state.events.forEach { event ->
        when (event) {
            GoogleDriveScreenEvent.RequireAuthentication -> {
                state.consumeEvent(event)
                requireAuthentication()
            }
        }
    }

    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val uiState = state.uiState
    val createFileRequest =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            state::onResult
        )
    GoogleDriveScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onProfileImageClick = state::onProfileImageClick,
        onFileClick = { file -> state.onFileClick(file, createFileRequest, onFolderClick) },
        onDialogDismissRequest = state::onDialogDismissRequest,
        onLogoutClick = state::onLogoutClick,
        onBackClick = onBackClick,
    )
}

@Parcelize
internal data class GoogleDriveScreenUiState(
    val profileUri: String = "",
    val googleAccountDialogUiState: GoogleAccountDialogUiState = GoogleAccountDialogUiState.Hide,
) : Parcelable

@Composable
internal fun GoogleDriveScreen(
    uiState: GoogleDriveScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onDialogDismissRequest: () -> Unit,
    onLogoutClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    Scaffold(
        topBar = {
            GoogleDriveTopAppBar(
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
    GoogleAccountDialog(
        uiState.googleAccountDialogUiState,
        onDismissRequest = onDialogDismissRequest,
        onLogoutClick = onLogoutClick,
    )
}
