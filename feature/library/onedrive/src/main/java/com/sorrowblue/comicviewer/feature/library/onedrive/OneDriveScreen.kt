package com.sorrowblue.comicviewer.feature.library.onedrive

import android.app.Activity
import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.onedrive.component.FileListItem
import com.sorrowblue.comicviewer.feature.library.onedrive.component.OneDriveTopAppBar
import com.sorrowblue.comicviewer.feature.library.onedrive.navigation.OneDriveArgs
import com.sorrowblue.comicviewer.feature.library.onedrive.section.OneDriveAccountDialog
import com.sorrowblue.comicviewer.feature.library.onedrive.section.OneDriveDialogUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LifecycleResumeEffect
import java.io.InputStream
import kotlinx.coroutines.flow.flowOf
import kotlinx.parcelize.Parcelize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OneDriveRoute(
    args: OneDriveArgs,
    onBackClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    state: OneDriveScreenState = rememberOneDriveScreenState(args),
) {
    val lazPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val uiState = state.uiState
    val context = LocalContext.current
    val createFileRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            state.onResult(it)
        }
    OneDriveScreen(
        lazyPagingItems = lazPagingItems,
        uiState = uiState,
        onBackClick = onBackClick,
        onSignInClick = { state.onLoginClick(context as Activity) },
        onProfileImageClick = state::onProfileImageClick,
        onFileClick = { state.onFileClick(it, createFileRequest, onFolderClick) },
        onDialogDismissRequest = state::onDialogDismissRequest,
        onLogoutClick = state::onLogoutClick,
    )

    LifecycleResumeEffect(action = state::onResume)
}

internal sealed interface OneDriveScreenUiState : Parcelable {

    @Parcelize
    data object Loading : OneDriveScreenUiState

    @Parcelize
    data class Login(val isRunning: Boolean = false) : OneDriveScreenUiState

    @Parcelize
    data class Loaded(
        val showDialog: Boolean = false,
        val oneDriveDialogUiState: OneDriveDialogUiState = OneDriveDialogUiState(),
        val path: String = "",
        val profileUri: suspend () -> InputStream? = { null },
    ) : OneDriveScreenUiState
}

@ExperimentalMaterial3Api
@Composable
private fun OneDriveScreen(
    lazyPagingItems: LazyPagingItems<File>,
    uiState: OneDriveScreenUiState = OneDriveScreenUiState.Loaded(),
    onBackClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onProfileImageClick: () -> Unit = {},
    onFileClick: (File) -> Unit = {},
    onDialogDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    when (uiState) {
        OneDriveScreenUiState.Loading -> LoadingOneDriveScreen(onCloseClick = onBackClick)
        is OneDriveScreenUiState.Login -> LoginOneDriveScreen(
            uiState = uiState,
            onCloseClick = onBackClick,
            onLoginClick = onSignInClick
        )

        is OneDriveScreenUiState.Loaded -> LoadedOneDriveScreen(
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
private fun LoadingOneDriveScreen(onCloseClick: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = com.sorrowblue.comicviewer.app.R.string.onedrive_title)) },
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
private fun LoginOneDriveScreen(
    uiState: OneDriveScreenUiState.Login = OneDriveScreenUiState.Login(),
    onCloseClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = com.sorrowblue.comicviewer.app.R.string.onedrive_title)) },
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
                Text(text = stringResource(id = R.string.onedrive_action_login))
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun LoadedOneDriveScreen(
    lazyPagingItems: LazyPagingItems<File> = fakeLazyPagingItems(),
    uiState: OneDriveScreenUiState.Loaded = OneDriveScreenUiState.Loaded(),
    onBackClick: () -> Unit = {},
    onProfileImageClick: () -> Unit = {},
    onFileClick: (File) -> Unit = {},
    onDialogDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
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
    if (uiState.showDialog) {
        OneDriveAccountDialog(
            uiState = uiState.oneDriveDialogUiState,
            onDismissRequest = onDialogDismissRequest,
            onLogoutClick = onLogoutClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewLoadedOneDriveScreen() {
    ComicTheme {
        LoadedOneDriveScreen()
    }
}

@Preview
@Composable
private fun PreviewLoginOneDriveScreen() {
    ComicTheme {
        LoginOneDriveScreen()
    }
}

@Preview
@Composable
private fun PreviewLoadingOneDriveScreen() {
    ComicTheme {
        LoadingOneDriveScreen()
    }
}

@Composable
private fun fakeLazyPagingItems() =
    flowOf(PagingData.from<File>(List(20) { bookFile(it) })).collectAsLazyPagingItems()

internal fun bookFile(index: Int) = BookFile(
    BookshelfId(0),
    "FakeBookName$index.zip",
    "/comic/example/",
    "/comic/example/FakeBookName$index.zip",
    0,
    0,
    "",
    50,
    123,
    0
)
