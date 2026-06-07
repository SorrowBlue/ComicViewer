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
import com.sorrowblue.comicviewer.framework.notification.DesktopNotification
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title_file_scan_completed
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title_thumbnail_scan_completed
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

@Composable
internal actual fun rememberBookshelfInfoContentsState(
    bookshelfFolder: BookshelfFolder,
): BookshelfInfoContentsState {
    val viewModel =
        assistedMetroViewModel<BookshelfInfoContentViewModel, BookshelfInfoContentViewModel.Factory> {
            create(bookshelfFolder)
        }
    val appState = LocalAppState.current
    return remember(bookshelfFolder, appState, viewModel) {
        BookshelfInfoContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            appState = appState,
            viewModel = viewModel
        )
    }.apply {
        lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    }
}

private class BookshelfInfoContentsStateImpl(
    private val bookshelfFolder: BookshelfFolder,
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
        scanFile()
    }

    override fun onScanThumbnailClick() {
        currentScanType = ScanType.Thumbnail
        scanThumbnail()
    }

    private fun scanFile() {
        showSnackbar()
        appState.coroutineScope.launch {
            viewModel.scanFile()
            DesktopNotification().notify(
                getString(Res.string.bookshelf_info_notification_title_file_scan_completed),
                bookshelfFolder.bookshelf.displayName,
            )
        }
    }

    private fun scanThumbnail() {
        showSnackbar()
        appState.coroutineScope.launch {
            viewModel.scanThumbnail()
            DesktopNotification().notify(
                getString(Res.string.bookshelf_info_notification_title_thumbnail_scan_completed),
                bookshelfFolder.bookshelf.displayName,
            )
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
}

private const val PageSize = 4

@OptIn(VisibleForAssistedInject::class)
@AssistedInject
class BookshelfInfoContentViewModel(
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

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(bookshelfFolder: BookshelfFolder): BookshelfInfoContentViewModel
    }
}
