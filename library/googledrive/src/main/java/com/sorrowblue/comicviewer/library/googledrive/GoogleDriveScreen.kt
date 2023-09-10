package com.sorrowblue.comicviewer.library.googledrive

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.api.services.drive.DriveScopes
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.framework.compose.LifecycleEffect
import com.sorrowblue.comicviewer.library.googledrive.component.GoogleDriveTopAppBar
import com.sorrowblue.comicviewer.library.googledrive.section.GoogleAccountDialog
import com.sorrowblue.comicviewer.library.googledrive.section.GoogleAccountDialogUiState
import logcat.logcat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GoogleDriveRoute(
    onFileClick: (File) -> Unit,
    viewModel: GoogleDriveViewModel = viewModel(
        factory = GoogleDriveViewModel.Factory(
            LocalContext.current,
        )
    )
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val activity = LocalContext.current as Activity
    val createFileRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                viewModel.enqueueDownload(it.data!!.data!!.toString(), viewModel.file)
            }
        }
    val activityResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                runCatching {
                    GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                    viewModel.refreshAccount()
                }.onFailure {
                    it.printStackTrace()
                    if (it is ApiException) {
                        logcat("APP") { "認証に失敗しました。(${it.statusCode})" }
                    } else if (it is RuntimeExecutionException && it.cause is ApiException) {
                        logcat("APP") { "認証に失敗しました。(${(it.cause as ApiException).statusCode})" }
                    } else {
                        logcat("APP") { "エラーが発生しました。" }
                    }
                }
            } else {
                logcat("APP") { "キャンセルしました。" }
            }
        }
    GoogleDriveScreen(
        uiState = uiState, lazyPagingItems = lazyPagingItems,
        onProfileImageClick = viewModel::onProfileImageClick,
        onSignInClick = {
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(Scope(DriveScopes.DRIVE_READONLY))
                    .build()
            activityResultLauncher.launch(
                GoogleSignIn.getClient(
                    activity,
                    googleSignInOptions
                ).signInIntent
            )
        },
        onFileClick = { file ->
            when (file) {
                is Book -> {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.putExtra(Intent.EXTRA_TITLE, file.name)
                    intent.type = "*/*"
                    viewModel.file = file
                    createFileRequest.launch(intent)
                }

                is Folder -> onFileClick(file)
            }
        },
        onDialogDismissRequest = viewModel::onDialogDismissRequest,
        onLogoutClick = {
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(Scope(DriveScopes.DRIVE_READONLY))
                    .build()
            GoogleSignIn.getClient(activity, googleSignInOptions).signOut().addOnCompleteListener {
                viewModel.onDialogDismissRequest()
                viewModel.refreshAccount()
            }
        }
    )
    LifecycleEffect(lifecycleObserver = viewModel)
}

internal data class GoogleDriveScreenUiState(
    val isAuthenticated: Boolean = false,
    val profileUri: String = "",
    val googleAccountDialogUiState: GoogleAccountDialogUiState = GoogleAccountDialogUiState.Hide
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GoogleDriveScreen(
    uiState: GoogleDriveScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit = {},
    onProfileImageClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onFileClick: (File) -> Unit = {},
    onDialogDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        if (uiState.isAuthenticated) {
            LazyColumn(contentPadding = innerPadding) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { it.path }) {
                    lazyPagingItems[it]?.let {
                        FileListItem(file = it, onClick = { onFileClick(it) })
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = onSignInClick) {
                    Text(text = "SignIn")
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
