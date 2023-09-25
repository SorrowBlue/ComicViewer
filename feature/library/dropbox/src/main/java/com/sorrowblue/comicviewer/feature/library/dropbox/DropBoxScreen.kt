package com.sorrowblue.comicviewer.feature.library.dropbox

import android.app.Activity
import android.content.Intent
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.feature.library.dropbox.component.DropBoxTopAppBar
import com.sorrowblue.comicviewer.feature.library.dropbox.data.DropBoxApiRepositoryImpl
import com.sorrowblue.comicviewer.feature.library.dropbox.section.DropBoxAccountDialog
import com.sorrowblue.comicviewer.feature.library.dropbox.section.DropBoxDialogUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DropBoxRoute(
    onBackClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    viewModel: DropBoxViewModel = viewModel(
        factory = DropBoxViewModel.Factory(
            LocalContext.current,
            DropBoxApiRepositoryImpl(LocalContext.current)
        )
    )
) {
    val lazPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val createFileRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                viewModel.enqueueDownload(it.data!!.data!!.toString(), viewModel.file)
            }
        }
    DropBoxScreen(
        lazyPagingItems = lazPagingItems,
        uiState = uiState,
        onBackClick = onBackClick,
        onSignInClick = { viewModel.login(context) },
        onProfileImageClick = viewModel::onProfileImageClick,
        onFileClick = {
            when (it) {
                is Book -> {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.putExtra(Intent.EXTRA_TITLE, it.name)
                    intent.type = "*/*"
                    viewModel.file = it
                    createFileRequest.launch(intent)
                }

                is Folder -> {
                    onFolderClick(it)
                }
            }
        },
        onDialogDismissRequest = viewModel::onDialogDismissRequest,
        onLogoutClick = viewModel::logout,
    )
    LifecycleEffect(lifecycleObserver = viewModel)
}

internal sealed interface DropBoxScreenUiState {
    data object Loading : DropBoxScreenUiState
    data class Login(val isRunning: Boolean = false) : DropBoxScreenUiState
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
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
    scrollBehavior: TopAppBarScrollBehavior
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
                key = lazyPagingItems.itemKey { it.path }) {
                lazyPagingItems[it]?.let {
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
fun FileListItem(file: File, onClick: () -> Unit) {
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
        modifier = Modifier.clickable(onClick = onClick)
    )
}
