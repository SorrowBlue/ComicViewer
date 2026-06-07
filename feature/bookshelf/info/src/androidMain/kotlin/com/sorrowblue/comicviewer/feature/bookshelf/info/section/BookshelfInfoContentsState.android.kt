package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.info.IntentLauncher
import com.sorrowblue.comicviewer.feature.bookshelf.info.NotificationPermissionRequester
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.feature.bookshelf.info.rememberNotificationPermissionRequester
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.FileScanWorker
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.ThumbnailScanWorker
import com.sorrowblue.comicviewer.framework.common.annotation.VisibleForAssistedInject
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_notification_settings
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file_no_notification
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails_no_notification
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.getString

@OptIn(VisibleForAssistedInject::class)
@AssistedInject
internal class BookshelfInfoContentViewModel(
    @Assisted private val bookshelfFolder: BookshelfFolder,
    private val workManager: WorkManager,
    private val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingBookshelfBookUseCase(
        PagingBookshelfBookUseCase.Request(
            bookshelfFolder.bookshelf.id,
            PagingConfig(PageSize),
        ),
    ).cachedIn(viewModelScope)

    val isScanningFile =
        FileScanWorker.getWorkInfosFlow(workManager, bookshelfFolder.bookshelf.id)
            .map { workInfos -> workInfos.any { !it.state.isFinished } }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                false,
            )
    val isScanningThumbnail =
        ThumbnailScanWorker.getWorkInfosFlow(workManager, bookshelfFolder.bookshelf.id)
            .map { workInfos -> workInfos.any { !it.state.isFinished } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun scanFile() {
        FileScanWorker.enqueueUniqueWork(workManager, bookshelfFolder.bookshelf.id)
    }

    fun scanThumbnail() {
        ThumbnailScanWorker.enqueueUniqueWork(workManager, bookshelfFolder.bookshelf.id)
    }
}

@AssistedFactory
@ManualViewModelAssistedFactoryKey
@ContributesIntoMap(AppScope::class)
internal interface BookshelfInfoContentViewModelFactory : ManualViewModelAssistedFactory {
    fun create(bookshelfFolder: BookshelfFolder): BookshelfInfoContentViewModel
}

@Composable
internal actual fun rememberBookshelfInfoContentsState(
    bookshelfFolder: BookshelfFolder,
): BookshelfInfoContentsState {
    val viewModel =
        assistedMetroViewModel<BookshelfInfoContentViewModel, BookshelfInfoContentViewModelFactory> {
            create(bookshelfFolder)
        }

    @SuppressLint("ContextCastToActivity")
    val activity = LocalContext.current as Activity
    val appState = LocalAppState.current
    val stateImpl = remember(bookshelfFolder, viewModel) {
        BookshelfInfoMainContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            viewModel = viewModel,
            context = activity,
            appState = appState,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
    stateImpl.notificationPermissionRequester = rememberNotificationPermissionRequester(
        stateImpl::onNotificationResult,
    )
    stateImpl.intentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    return stateImpl
}

@OptIn(VisibleForAssistedInject::class)
private class BookshelfInfoMainContentsStateImpl(
    bookshelfFolder: BookshelfFolder,
    private val viewModel: BookshelfInfoContentViewModel,
    private val context: Context,
    private val appState: AppState,
) : BookshelfInfoContentsState,
    IntentLauncher {
    override lateinit var lazyPagingItems: LazyPagingItems<BookThumbnail>

    private lateinit var currentScanType: ScanType

    override lateinit var intentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    lateinit var notificationPermissionRequester: NotificationPermissionRequester

    override val events = EventFlow<BookshelfInfoContentsEvent>()

    override var uiState by mutableStateOf(
        BookshelfInfoContentsUiState(
            bookshelf = bookshelfFolder.bookshelf,
            folder = bookshelfFolder.folder,
        ),
    )
        private set

    init {
        viewModel.isScanningFile.onEach {
            uiState = uiState.copy(isScanningFile = it)
        }.launchIn(appState.coroutineScope)
        viewModel.isScanningThumbnail.onEach {
            uiState = uiState.copy(isScanningThumbnail = it)
        }.launchIn(appState.coroutineScope)
    }

    override fun onScanFileClick() {
        currentScanType = ScanType.File
        notificationPermissionRequester.requestPermission(
            action = ::scanFile,
            showInContextUI = {
                events.tryEmit(
                    BookshelfInfoContentsEvent.ShowNotificationPermissionRationale(
                        ScanType.File,
                    ),
                )
            },
        )
    }

    override fun onScanThumbnailClick() {
        currentScanType = ScanType.Thumbnail
        notificationPermissionRequester.requestPermission(
            action = ::scanThumbnail,
            showInContextUI = {
                events.tryEmit(
                    BookshelfInfoContentsEvent.ShowNotificationPermissionRationale(
                        ScanType.Thumbnail,
                    ),
                )
            },
        )
    }

    fun onNotificationResult(result: Boolean) {
        logcat { "onNotificationResult(result: $result) notificationRequestType: $currentScanType" }
        if (result) {
            when (currentScanType) {
                ScanType.File -> onScanFileClick()
                ScanType.Thumbnail -> onScanThumbnailClick()
            }
        } else {
            when (currentScanType) {
                ScanType.File -> scanFile()
                ScanType.Thumbnail -> scanThumbnail()
            }
        }
    }

    private fun scanFile() {
        showSnackbar()
        viewModel.scanFile()
    }

    private fun scanThumbnail() {
        showSnackbar()
        viewModel.scanThumbnail()
    }

    private fun showSnackbar() {
        appState.coroutineScope.launch {
            if (notificationPermissionRequester.checkNotificationPermission()) {
                appState.snackbarHostState.showSnackbar(
                    getString(
                        when (currentScanType) {
                            ScanType.File -> Res.string.bookshelf_info_label_scanning_file
                            ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails
                        },
                    ),
                )
            } else {
                val result = appState.snackbarHostState.showSnackbar(
                    message = getString(
                        when (currentScanType) {
                            ScanType.File -> Res.string.bookshelf_info_label_scanning_file_no_notification
                            ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails_no_notification
                        },
                    ),
                    actionLabel = getString(Res.string.bookshelf_info_label_notification_settings),
                    duration = SnackbarDuration.Long,
                )
                when (result) {
                    SnackbarResult.Dismissed -> Unit

                    SnackbarResult.ActionPerformed -> {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        launchIntent(intent)
                    }
                }
            }
        }
    }
}

private const val PageSize = 4
