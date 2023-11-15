package com.sorrowblue.comicviewer.feature.library.dropbox

import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.dropbox.component.DropBoxTopAppBar
import com.sorrowblue.comicviewer.feature.library.dropbox.navigation.DropBoxArgs
import com.sorrowblue.comicviewer.feature.library.dropbox.section.DropBoxAccountDialog
import com.sorrowblue.comicviewer.feature.library.dropbox.section.DropBoxDialogUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DropBoxRoute(
    args: DropBoxArgs,
    onBackClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    state: DropBoxScreenState = rememberDropBoxScreenState(args),
) {
    val uiState = state.uiState
    val lazPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val createFileRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        state::onResult
    )
    DropBoxScreen(
        lazyPagingItems = lazPagingItems,
        uiState = uiState,
        onBackClick = onBackClick,
        onSignInClick = state::onSignInClick,
        onProfileImageClick = state::onProfileImageClick,
        onFileClick = { state.onFileClick(it, createFileRequest, onFolderClick) },
        onDialogDismissRequest = state::onDialogDismissRequest,
        onLogoutClick = state::onLogoutClick,
    )
    LifecycleEffect(targetEvent = Lifecycle.Event.ON_RESUME, action = state::onResume)
}

internal sealed interface DropBoxScreenUiState : Parcelable {
    @Parcelize
    data object Loading : DropBoxScreenUiState

    @Parcelize
    data class Login(val isRunning: Boolean = false) : DropBoxScreenUiState

    @Parcelize
    data class Loaded(
        val path: String = "",
        val dropBoxDialogUiState: DropBoxDialogUiState = DropBoxDialogUiState.Hide,
        val profileUri: String = "",
    ) : DropBoxScreenUiState
}

@ExperimentalMaterial3Api
@Composable
private fun DropBoxScreen(
    lazyPagingItems: LazyPagingItems<File>,
    uiState: DropBoxScreenUiState = DropBoxScreenUiState.Loaded(),
    onBackClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onProfileImageClick: () -> Unit = {},
    onFileClick: (File) -> Unit = {},
    onDialogDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    when (uiState) {
        DropBoxScreenUiState.Loading -> LoadingDropBoxScreen(onCloseClick = onBackClick)
        is DropBoxScreenUiState.Login -> LoginDropBoxScreen(
            uiState = uiState,
            onCloseClick = onBackClick,
            onLoginClick = onSignInClick
        )

        is DropBoxScreenUiState.Loaded -> LoadedDropBoxScreen(
            lazyPagingItems = lazyPagingItems,
            uiState = uiState,
            onBackClick = onBackClick,
            onProfileImageClick = onProfileImageClick,
            onFileClick = onFileClick,
            onDialogDismissRequest = onDialogDismissRequest,
            onLogoutClick = onLogoutClick,
            scrollBehavior = scrollBehavior,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingDropBoxScreen(onCloseClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "DropBox") },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(imageVector = ComicIcons.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { contentPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginDropBoxScreen(
    uiState: DropBoxScreenUiState.Login,
    onCloseClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "DropBox") },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(imageVector = ComicIcons.Close, contentDescription = "Close")
                    }
                }
            )
        },
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            AnimatedVisibility(visible = uiState.isRunning) {
                LinearProgressIndicator()
            }
            Button(enabled = !uiState.isRunning, onClick = onLoginClick) {
                Text(text = "Login")
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun LoadedDropBoxScreen(
    lazyPagingItems: LazyPagingItems<File>,
    uiState: DropBoxScreenUiState.Loaded,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onDialogDismissRequest: () -> Unit,
    onLogoutClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
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

@Composable
fun FileListItem(file: File, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(text = file.name) },
        trailingContent = {
            Text(text = file.size.toString())
        },
        leadingContent = {
            AsyncImage(
                model = file.params["iconLink"],
                contentDescription = null,
                Modifier.size(24.dp)
            )
        },
        modifier = modifier.clickable(onClick = onClick)
    )
}
