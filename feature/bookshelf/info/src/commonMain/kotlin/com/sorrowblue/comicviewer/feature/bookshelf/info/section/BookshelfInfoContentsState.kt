/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.NotificationPermissionRequester
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.feature.bookshelf.info.rememberNotificationPermissionRequester
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.permission.localnetwork.rememberLocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_notification_settings
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file_no_notification
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails_no_notification
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.getString

internal sealed interface BookshelfInfoContentsEvent {
    data class ShowNotificationPermissionRationale(val type: ScanType) :
        BookshelfInfoContentsEvent
}

internal interface BookshelfInfoContentsState {
    val uiState: BookshelfInfoContentsUiState
    val lazyPagingItems: LazyPagingItems<BookThumbnail>
    val events: EventFlow<BookshelfInfoContentsEvent>
    val localNetworkPermissionRequester: LocalNetworkPermissionRequester

    fun onScanFileClick()

    fun onScanThumbnailClick()
}

@Composable
internal fun rememberBookshelfInfoContentsState(
    bookshelfFolder: BookshelfFolder,
): BookshelfInfoContentsState {
    val viewModel =
        assistedMetroViewModel<BookshelfInfoContentViewModel, BookshelfInfoContentViewModel.Factory> {
            create(bookshelfFolder)
        }
    val appState = LocalAppState.current
    val coroutineScope = rememberCoroutineScope()
    val stateImpl = remember(bookshelfFolder, viewModel, appState) {
        BookshelfInfoContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            coroutineScope = coroutineScope,
            viewModel = viewModel,
            appState = appState,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
    stateImpl.notificationPermissionRequester = rememberNotificationPermissionRequester(
        stateImpl::onNotificationResult,
    )
    stateImpl.localNetworkPermissionRequester = rememberLocalNetworkPermissionRequester(false)
    return stateImpl
}

private class BookshelfInfoContentsStateImpl(
    bookshelfFolder: BookshelfFolder,
    private val coroutineScope: CoroutineScope,
    private val viewModel: BookshelfInfoContentViewModel,
    private val appState: AppState,
) : BookshelfInfoContentsState {

    override lateinit var lazyPagingItems: LazyPagingItems<BookThumbnail>
    lateinit var notificationPermissionRequester: NotificationPermissionRequester
    override lateinit var localNetworkPermissionRequester: LocalNetworkPermissionRequester

    private lateinit var currentScanType: ScanType

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
        }.launchIn(coroutineScope)
        viewModel.isScanningThumbnail.onEach {
            uiState = uiState.copy(isScanningThumbnail = it)
        }.launchIn(coroutineScope)
    }

    override fun onScanFileClick() {
        val hasPermission = when (uiState.bookshelf) {
            is DeviceStorage -> true
            ShareContents -> true
            is SmbServer -> localNetworkPermissionRequester.checkPermission()
        }
        if (!hasPermission) {
            return
        }
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
        val hasPermission = when (uiState.bookshelf) {
            is DeviceStorage -> true
            ShareContents -> true
            is SmbServer -> localNetworkPermissionRequester.checkPermission()
        }
        if (!hasPermission) {
            return
        }
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
        coroutineScope.launch {
            if (notificationPermissionRequester.checkNotificationPermission()) {
                appState.showSnackbar(
                    message = getString(
                        when (currentScanType) {
                            ScanType.File -> Res.string.bookshelf_info_label_scanning_file
                            ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails
                        },
                    ),
                )
            } else {
                appState.showSnackbar(
                    message = getString(
                        when (currentScanType) {
                            ScanType.File -> Res.string.bookshelf_info_label_scanning_file_no_notification
                            ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails_no_notification
                        },
                    ),
                    actionLabel = getString(Res.string.bookshelf_info_label_notification_settings),
                    duration = SnackbarDuration.Long,
                    onActionPerformed = notificationPermissionRequester::openNotificationSettings,
                )
            }
        }
    }
}
