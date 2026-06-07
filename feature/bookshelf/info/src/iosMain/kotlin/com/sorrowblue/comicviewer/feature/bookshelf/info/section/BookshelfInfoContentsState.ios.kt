package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.framework.common.annotation.VisibleForAssistedInject
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import platform.Foundation.NSRunLoop
import platform.Foundation.NSThread
import platform.Foundation.performBlock
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine

@Composable
internal actual fun rememberBookshelfInfoContentsState(
    bookshelfFolder: BookshelfFolder,
): BookshelfInfoContentsState {
    val viewModel =
        assistedMetroViewModel<BookshelfInfoContentViewModel, BookshelfInfoContentViewModelFactory> {
            create(bookshelfFolder)
        }
    val appState = LocalAppState.current
    return remember(bookshelfFolder, appState, viewModel) {
        BookshelfInfoContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            appState = appState,
            viewModel = viewModel,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

private class BookshelfInfoContentsStateImpl(
    bookshelfFolder: BookshelfFolder,
    private val appState: AppState,
    private val viewModel: BookshelfInfoContentViewModel
) : BookshelfInfoContentsState {
    override lateinit var lazyPagingItems: LazyPagingItems<BookThumbnail>

    private lateinit var currentScanType: ScanType

    override val events = EventFlow<BookshelfInfoContentsEvent>()

    override var uiState by mutableStateOf(
        BookshelfInfoContentsUiState(
            bookshelf = bookshelfFolder.bookshelf,
            folder = bookshelfFolder.folder,
        ),
    )
        private set

    override fun onScanFileClick() {
        currentScanType = ScanType.File
        appState.coroutineScope.launch {
            val status = NotificationPermissionDelegate().providePermission()
            when (status) {
                PermissionState.NotDetermined -> {
                }

                PermissionState.Granted -> {
                    notify()
                    scanFile()
                }

                PermissionState.DeniedAlways -> {
                    events.tryEmit(
                        BookshelfInfoContentsEvent.ShowNotificationPermissionRationale(
                            ScanType.File,
                        ),
                    )
                    scanFile()
                }
            }
        }
    }

    override fun onScanThumbnailClick() {
        currentScanType = ScanType.Thumbnail
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or
                    UNAuthorizationOptionSound or
                    UNAuthorizationOptionBadge,
            completionHandler = { granted, _ ->
                if (granted) {
                    notify()
                } else {
                    events.tryEmit(
                        BookshelfInfoContentsEvent.ShowNotificationPermissionRationale(
                            ScanType.File,
                        ),
                    )
                }
                scanThumbnail()
            },
        )
    }

    private suspend fun getPermissionStatus(): UNAuthorizationStatus {
        val currentCenter = UNUserNotificationCenter.currentNotificationCenter()
        return suspendCoroutine { continuation ->
            currentCenter.getNotificationSettingsWithCompletionHandler(
                mainContinuation { settings ->
                    continuation.resumeWith(
                        Result.success(
                            settings?.authorizationStatus ?: UNAuthorizationStatusNotDetermined,
                        ),
                    )
                },
            )
        }
    }

    private fun scanFile() {
        showSnackbar()
        appState.coroutineScope.launch {
            viewModel.scanFile()
        }
    }

    private fun scanThumbnail() {
        showSnackbar()
        appState.coroutineScope.launch {
            viewModel.scanThumbnail()
        }
    }

    private fun showSnackbar() {
        appState.coroutineScope.launch {
            appState.snackbarHostState.showSnackbar(
                message = getString(
                    when (currentScanType) {
                        ScanType.File -> Res.string.bookshelf_info_label_scanning_file
                        ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails
                    },
                ),
            )
        }
    }

    private fun notify() {
        val id = 0
        val content = UNMutableNotificationContent()
            .apply {
                setTitle(title)
                setBody(body)
                setSound(UNNotificationSound.defaultSound())
                // setUserInfo(config.userInfo)
            }

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "$id",
            content = content,
            trigger = null,
        )
        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request = request) { error ->
                println("IosNotificationManager---$error")
            }
    }
}

private const val PageSize = 4

object MainRunDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) =
        NSRunLoop.mainRunLoop.performBlock { block.run() }
}

inline fun <T1> mainContinuation(crossinline block: (T1) -> Unit): (T1) -> Unit = { arg1 ->
    if (NSThread.isMainThread()) {
        block.invoke(arg1)
    } else {
        MainRunDispatcher.run {
            block.invoke(arg1)
        }
    }
}

@Suppress("LESS_VISIBLE_TYPE_ACCESS_IN_INLINE_WARNING")
inline fun <T1, T2> mainContinuation(crossinline block: (T1, T2) -> Unit): (T1, T2) -> Unit =
    { arg1, arg2 ->
        if (NSThread.isMainThread()) {
            block.invoke(arg1, arg2)
        } else {
            MainRunDispatcher.run {
                block.invoke(arg1, arg2)
            }
        }
    }

@OptIn(VisibleForAssistedInject::class)
@AssistedInject
internal class BookshelfInfoContentViewModel(
    @Assisted private val bookshelfFolder: BookshelfFolder,
    private val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase,
    private val scanBookshelfUseCase: ScanBookshelfUseCase,
    private val regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingBookshelfBookUseCase(
        PagingBookshelfBookUseCase.Request(
            bookshelfFolder.bookshelf.id,
            PagingConfig(PageSize),
        ),
    ).cachedIn(viewModelScope)

    suspend fun scanFile() {
        scanBookshelfUseCase.invoke(
            ScanBookshelfUseCase.Request(bookshelfId = bookshelfFolder.bookshelf.id) { _, _ -> },
        )
    }

    suspend fun scanThumbnail() {
        val request =
            RegenerateThumbnailsUseCase.Request(bookshelfId = bookshelfFolder.bookshelf.id) { _, _, _ ->
            }
        regenerateThumbnailsUseCase(request)
    }
}

@AssistedFactory
@ManualViewModelAssistedFactoryKey
@ContributesIntoMap(AppScope::class)
internal interface BookshelfInfoContentViewModelFactory : ManualViewModelAssistedFactory {
    fun create(bookshelfFolder: BookshelfFolder): BookshelfInfoContentViewModel
}
