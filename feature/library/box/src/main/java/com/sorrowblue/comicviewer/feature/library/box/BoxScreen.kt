package com.sorrowblue.comicviewer.feature.library.box

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.feature.library.box.component.BoxTopAppBar
import com.sorrowblue.comicviewer.feature.library.box.component.FileListItem
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.feature.library.box.section.BoxAccountDialog
import com.sorrowblue.comicviewer.feature.library.box.section.BoxDialogUiState
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BoxRoute(
    onBackClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    viewModel: BoxViewModel = viewModel(
        factory = BoxViewModel.Factory(
            LocalContext.current,
            BoxApiRepository.getInstance(LocalContext.current)
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
    BoxScreen(
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
}

internal sealed interface BoxScreenUiState {
    data object Loading : BoxScreenUiState
    data class Login(val isRunning: Boolean = false) : BoxScreenUiState
    data class Loaded(
        val path: String = "",
        val boxDialogUiState: BoxDialogUiState = BoxDialogUiState.Hide,
        val profileUri: String = "",
        val token: String = "",
    ) : BoxScreenUiState
}

@ExperimentalMaterial3Api
@Composable
private fun BoxScreen(
    lazyPagingItems: LazyPagingItems<File>,
    uiState: BoxScreenUiState = BoxScreenUiState.Loaded(),
    onBackClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onProfileImageClick: () -> Unit = {},
    onFileClick: (File) -> Unit = {},
    onDialogDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    when (uiState) {
        BoxScreenUiState.Loading -> LoadingBoxScreen(onCloseClick = onBackClick)
        is BoxScreenUiState.Login -> LoginBoxScreen(
            uiState = uiState,
            onCloseClick = onBackClick,
            onLoginClick = onSignInClick
        )

        is BoxScreenUiState.Loaded -> LoadedBoxScreen(
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
private fun LoadingBoxScreen(onCloseClick: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Box") },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(imageVector = Icons.TwoTone.Close, contentDescription = "Close")
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
private fun LoginBoxScreen(
    uiState: BoxScreenUiState.Login = BoxScreenUiState.Login(),
    onCloseClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Box") },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(imageVector = Icons.TwoTone.Close, contentDescription = "Close")
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
private fun LoadedBoxScreen(
    lazyPagingItems: LazyPagingItems<File> = fakeLazyPagingItems(),
    uiState: BoxScreenUiState.Loaded = BoxScreenUiState.Loaded(),
    onBackClick: () -> Unit = {},
    onProfileImageClick: () -> Unit = {},
    onFileClick: (File) -> Unit = {},
    onDialogDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
    BoxAccountDialog(
        uiState = uiState.boxDialogUiState,
        onDismissRequest = onDialogDismissRequest,
        onLogoutClick = onLogoutClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewLoadedBoxScreen() {
    AppMaterialTheme {
        LoadedBoxScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewLoginBoxScreen() {
    AppMaterialTheme {
        LoginBoxScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewLoadingBoxScreen() {
    AppMaterialTheme {
        LoadingBoxScreen()
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
