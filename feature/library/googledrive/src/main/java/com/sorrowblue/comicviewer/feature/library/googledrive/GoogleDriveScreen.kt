package com.sorrowblue.comicviewer.feature.library.googledrive

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
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
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
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
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.googledrive.component.GoogleDriveTopAppBar
import com.sorrowblue.comicviewer.feature.library.googledrive.navigation.GoogleDriveArgs
import com.sorrowblue.comicviewer.feature.library.googledrive.section.GoogleAccountDialog
import com.sorrowblue.comicviewer.feature.library.googledrive.section.GoogleAccountDialogUiState
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent

@Stable
internal class GoogleDriveScreenState(
    args: GoogleDriveArgs,
    context: Context,
    private val scope: CoroutineScope,
    private val repository: GoogleDriveApiRepository,
    internal var file: File? = null,
    restoreUiState: GoogleDriveScreenUiState = GoogleDriveScreenUiState(),
) : KoinComponent {

    private val path = args.path

    var uiState by mutableStateOf(restoreUiState)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = repository.driverServiceFlow.flatMapLatest {
        Pager(PagingConfig(20)) { GoogleDrivePagingSource(it, path) }.flow
    }.cachedIn(scope)

    fun onResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK && activityResult.data?.data != null) {
            enqueueDownload(activityResult.data!!.data!!.toString(), file!!)
        }
    }

    init {
        repository.googleSignInAccount.onEach {
            uiState = uiState.copy(
                isAuthenticated = it != null,
                profileUri = it?.photoUrl?.toString().orEmpty()
            )
        }.launchIn(scope)

        val notificationManager = NotificationManagerCompat.from(context)
        val name = context.getString(R.string.googledrive_name_download_status)
        val descriptionText = context.getString(R.string.googledrive_desc_notify_download_status)
        val channel = NotificationChannelCompat.Builder(
            ChannelID.DOWNLOAD.id,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(name).setDescription(descriptionText)
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    private val workManager = WorkManager.getInstance(context)
    fun enqueueDownload(outputUri: String, file: File) {
        val request = OneTimeWorkRequestBuilder<DriveDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "fileId" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        workManager.enqueue(request)
    }

    fun onSignInResult(result: ActivityResult) {
        repository.signInResult(result) {
            refreshAccount()
        }
    }

    fun onProfileImageClick() {
        val account = repository.googleSignInAccount.value ?: return
        uiState = uiState.copy(
            googleAccountDialogUiState = GoogleAccountDialogUiState.Show(
                photoUrl = account.photoUrl?.toString().orEmpty(),
                name = account.displayName.orEmpty()
            )
        )
    }

    fun onDialogDismissRequest() {
        uiState = uiState.copy(googleAccountDialogUiState = GoogleAccountDialogUiState.Hide)
    }

    fun refreshAccount() {
        repository.updateAccount()
    }

    fun onStart() {
        refreshAccount()
    }

    fun onFileClick(
        file: File,
        createFileRequest: ManagedActivityResultLauncher<Intent, ActivityResult>,
        onFileClick: (File) -> Unit,
    ) {
        when (file) {
            is Book -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(Intent.EXTRA_TITLE, file.name)
                intent.type = "*/*"
                this.file = file
                createFileRequest.launch(intent)
            }

            is Folder -> onFileClick(file)
        }
    }

    fun onLogoutClick(activity: Activity) {
        repository.logout(activity) {
            onDialogDismissRequest()
            refreshAccount()
        }
    }

    fun onSignInClick(
        activity: Activity,
        activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    ) {
        repository.startSignIn(activity, activityResultLauncher)
    }
}

@Composable
internal fun rememberGoogleDriveScreenState(
    args: GoogleDriveArgs,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: GoogleDriveApiRepository = koinInject(),
) = rememberSaveable(
    saver = mapSaver(
        save = { mapOf("uiState" to it.uiState, "file" to it.file) },
        restore = {
            GoogleDriveScreenState(
                restoreUiState = it["uiState"] as GoogleDriveScreenUiState,
                file = it["file"] as? File,
                args = args,
                scope = scope,
                context = context,
                repository = repository
            )
        }
    )
) {
    GoogleDriveScreenState(
        args = args,
        scope = scope,
        context = context,
        repository = repository
    )
}

@Composable
internal fun GoogleDriveRoute(
    args: GoogleDriveArgs,
    onFileClick: (File) -> Unit,
    onBackClick: () -> Unit,
    state: GoogleDriveScreenState = rememberGoogleDriveScreenState(args = args),
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val uiState = state.uiState
    val activity = LocalContext.current as Activity
    val createFileRequest =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            state::onResult
        )
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = state::onSignInResult
    )
    GoogleDriveScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onProfileImageClick = state::onProfileImageClick,
        onSignInClick = { state.onSignInClick(activity, activityResultLauncher) },
        onFileClick = { file -> state.onFileClick(file, createFileRequest, onFileClick) },
        onDialogDismissRequest = state::onDialogDismissRequest,
        onLogoutClick = { state.onLogoutClick(activity) },
        onBackClick = onBackClick,
    )
    LifecycleEffect(targetEvent = Lifecycle.Event.ON_START, action = state::onStart)
}

@Parcelize
internal data class GoogleDriveScreenUiState(
    val isAuthenticated: Boolean = false,
    val profileUri: String = "",
    val googleAccountDialogUiState: GoogleAccountDialogUiState = GoogleAccountDialogUiState.Hide,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GoogleDriveScreen(
    uiState: GoogleDriveScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onProfileImageClick: () -> Unit,
    onSignInClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onDialogDismissRequest: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
                    key = lazyPagingItems.itemKey { it.path }
                ) { index ->
                    lazyPagingItems[index]?.let {
                        FileListItem(file = it, onClick = { onFileClick(it) })
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = onSignInClick) {
                    Text(text = stringResource(R.string.googledrive_action_login))
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
