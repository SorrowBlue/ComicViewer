package com.sorrowblue.comicviewer.feature.library.box

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.box.sdk.BoxAPIConnection
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.box.component.BoxTopAppBar
import com.sorrowblue.comicviewer.feature.library.box.component.FileListItem
import com.sorrowblue.comicviewer.feature.library.box.component.FileListItemSh
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.feature.library.box.navigation.BoxArgs
import com.sorrowblue.comicviewer.feature.library.box.section.BoxAccountDialog
import com.sorrowblue.comicviewer.feature.library.box.section.BoxDialogUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import java.net.URI
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.compose.koinInject

@Composable
internal fun rememberBoxScreenState(
    args: BoxArgs,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: BoxApiRepository = koinInject(),
): BoxScreenState = rememberSaveable(
    saver = mapSaver(
        save = { mapOf("uiState" to it.uiState, "book" to it.book) },
        restore = {
            BoxScreenState(
                args = args,
                uiState = it["uiState"] as BoxScreenUiState,
                book = it["book"] as? Book,
                context = context,
                scope = scope,
                repository = repository
            )
        }
    )
) {
    BoxScreenState(
        args = args,
        uiState = BoxScreenUiState.Loading,
        context = context,
        scope = scope,
        repository = repository
    )
}

@Stable
internal class BoxScreenState(
    args: BoxArgs,
    uiState: BoxScreenUiState,
    private val context: Context,
    private val scope: CoroutineScope,
    private val repository: BoxApiRepository,
    internal var book: Book? = null,
) {

    var uiState by mutableStateOf(uiState)
        private set

    init {
        repository.isAuthenticated().onEach {
            if (it) {
                this.uiState = BoxScreenUiState.Loaded(path = args.path)
            } else {
                this.uiState = BoxScreenUiState.Login(false)
            }
        }.launchIn(scope)
        repository.userInfoFlow.onEach {
            if (it != null) {
                if (this.uiState is BoxScreenUiState.Loaded) {
                    this.uiState = (this.uiState as BoxScreenUiState.Loaded).copy(
                        profileUri = "https://api.box.com/2.0/users/${it.id}/avatar",
                        token = repository.accessToken()
                    )
                }
            }
        }.launchIn(scope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = repository.userInfoFlow.filterNotNull().flatMapLatest {
        Pager(PagingConfig(20)) { BoxPagingSource(args.path, repository) }.flow
            .cachedIn(scope)
    }

    fun onClickLogin() {
        val state = Random.nextInt(20).toString()
        val url = BoxAPIConnection.getAuthorizationURL(
            "nihdm7dthg9lm7m3b41bpw7jp7b0lb9z",
            URI.create("https://comicviewer.sorrowblue.com/box/oauth2"),
            state,
            null
        )
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url.toString()))
    }

    fun onProfileImageClick() {
        if (uiState is BoxScreenUiState.Loaded) {
            scope.launch {
                repository.userInfoFlow.first()?.let {
                    uiState = (uiState as BoxScreenUiState.Loaded).copy(
                        showDialog = true,
                        boxDialogUiState = BoxDialogUiState(it.avatarURL, it.name)
                    )
                }
            }
        }
    }

    fun onDialogDismissRequest() {
        if (uiState is BoxScreenUiState.Loaded) {
            uiState = (uiState as BoxScreenUiState.Loaded).copy(showDialog = false)
        }
    }

    fun onLogoutClick() {
        scope.launch {
            repository.signOut()
        }
    }

    private val workManager = WorkManager.getInstance(context)

    fun enqueueDownload(outputUri: String, file: Book) {
        val request = OneTimeWorkRequestBuilder<BoxDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "path" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        workManager.enqueue(request)
    }

    fun onClickFile(
        file: File,
        createFileRequest: ManagedActivityResultLauncher<Intent, ActivityResult>,
        onFolderClick: (Folder) -> Unit,
    ) {
        when (file) {
            is Book -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(Intent.EXTRA_TITLE, file.name)
                intent.type = "*/*"
                this.book = file
                createFileRequest.launch(intent)
            }

            is Folder -> {
                onFolderClick(file)
            }
        }
    }

    fun onResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK && activityResult.data?.data != null) {
            enqueueDownload(activityResult.data!!.data!!.toString(), book!!)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BoxRoute(
    args: BoxArgs,
    onBackClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    state: BoxScreenState = rememberBoxScreenState(args = args),
) {
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
        onSignInClick = state::onClickLogin,
        onProfileImageClick = state::onProfileImageClick,
        onFileClick = { state.onClickFile(it, createFileRequest, onFolderClick) },
        onDialogDismissRequest = state::onDialogDismissRequest,
        onLogoutClick = state::onLogoutClick,
    )
}

internal sealed interface BoxScreenUiState : Parcelable {

    @Parcelize
    data object Loading : BoxScreenUiState

    @Parcelize
    data class Login(val isRunning: Boolean = false) : BoxScreenUiState

    @Parcelize
    data class Loaded(
        val path: String = "",
        val showDialog: Boolean = false,
        val boxDialogUiState: BoxDialogUiState = BoxDialogUiState(),
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
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
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
                title = { Text(text = stringResource(R.string.box_title2)) },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = ComicIcons.Close,
                            contentDescription = stringResource(R.string.box_action_close)
                        )
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
                title = { Text(text = stringResource(R.string.box_title2)) },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = ComicIcons.Close,
                            contentDescription = stringResource(R.string.box_action_close)
                        )
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
                Text(text = stringResource(R.string.box_action_login))
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun LoadedBoxScreen(
    lazyPagingItems: LazyPagingItems<File>,
    uiState: BoxScreenUiState.Loaded = BoxScreenUiState.Loaded(),
    onBackClick: () -> Unit = {},
    onProfileImageClick: () -> Unit = {},
    onFileClick: (File) -> Unit = {},
    onDialogDismissRequest: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
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
                key = lazyPagingItems.itemKey { it.path }
            ) {
                val item = lazyPagingItems[it]
                if (item != null) {
                    FileListItem(
                        file = item,
                        onClick = { onFileClick(item) }
                    )
                } else {
                    FileListItemSh()
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
