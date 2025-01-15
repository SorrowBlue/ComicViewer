package com.sorrowblue.comicviewer.file

import android.os.Parcelable
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.file.component.FileAttributeChips
import com.sorrowblue.comicviewer.file.section.FileInfoList
import com.sorrowblue.comicviewer.file.section.FileInfoThumbnail
import com.sorrowblue.comicviewer.file.section.SheetActionButtons
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffoldDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCanonicalScaffold
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class ReadLaterUiState(val checked: Boolean = false, val loading: Boolean = false)

data class FileInfoUiState(
    val file: File,
    val attribute: FileAttribute? = null,
    val readLaterUiState: ReadLaterUiState = ReadLaterUiState(),
    val isOpenFolderEnabled: Boolean = false,
)

sealed interface FileInfoSheetNavigator {
    data object Back : FileInfoSheetNavigator
    data class Favorite(val file: File) : FileInfoSheetNavigator
    data class OpenFolder(val file: File) : FileInfoSheetNavigator
}

internal sealed interface FileInfoSheetAction {
    data object Close : FileInfoSheetAction
    data object ReadLater : FileInfoSheetAction
    data object Favorite : FileInfoSheetAction
    data object OpenFolder : FileInfoSheetAction
}

interface FileKeyState {
    val file: File?
}

@HiltViewModel
internal class FileKeyViewModel @Inject constructor(
    val getFileUseCase: GetFileUseCase,
) : ViewModel()

@Composable
internal fun rememberFileKeyState(
    fileKey: File.Key,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FileKeyViewModel = hiltViewModel(),
): FileKeyState {
    return remember(fileKey) { FileKeyStateImpl(fileKey, scope, viewModel.getFileUseCase) }
}

private class FileKeyStateImpl(
    fileKey: File.Key,
    scope: CoroutineScope,
    getFileUseCase: GetFileUseCase,
) : FileKeyState {
    override var file by mutableStateOf<File?>(null)
        private set

    init {
        getFileUseCase(GetFileUseCase.Request(fileKey.bookshelfId, fileKey.path)).onEach {
            file = it.dataOrNull()
        }.launchIn(scope)
    }
}

@Composable
fun FileInfoSheet(
    fileKey: File.Key,
    onAction: (FileInfoSheetNavigator) -> Unit,
    isOpenFolderEnabled: Boolean = false,
) {
    val state = rememberFileKeyState(fileKey)
    state.file?.let {
        FileInfoSheet(it, onAction, isOpenFolderEnabled)
    }
}

@Composable
fun FileInfoSheet(
    file: File,
    onAction: (FileInfoSheetNavigator) -> Unit,
    isOpenFolderEnabled: Boolean = false,
) {
    val state = rememberFileInfoSheetState(file = file, isOpenFolderEnabled = isOpenFolderEnabled)
    FileInfoSheet(
        uiState = state.uiState,
        lazyPagingItems = state.lazyPagingItems,
        onAction = state::onAction,
    )
    LaunchedEventEffect(state.event) {
        when (it) {
            FileInfoSheetStateEvent.Close -> onAction(FileInfoSheetNavigator.Back)
            is FileInfoSheetStateEvent.Favorite -> onAction(FileInfoSheetNavigator.Favorite(it.file))
            is FileInfoSheetStateEvent.OpenFolder -> onAction(FileInfoSheetNavigator.OpenFolder(it.file))
        }
    }
}

@Composable
internal fun FileInfoSheet(
    uiState: FileInfoUiState,
    onAction: (FileInfoSheetAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    lazyPagingItems: LazyPagingItems<BookThumbnail>? = null,
) {
    val file = uiState.file
    ExtraPaneScaffold(
        title = { Text(text = file.name) },
        onCloseClick = { onAction(FileInfoSheetAction.Close) },
        modifier = modifier
    ) { contentPadding ->
        Box {
            Column(
                Modifier
                    .fillMaxSize()
                    .drawVerticalScrollbar(scrollState)
                    .verticalScroll(scrollState)
                    .padding(top = contentPadding.calculateTopPadding())
            ) {
                FileInfoThumbnail(
                    file = file,
                    lazyPagingItems = lazyPagingItems,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                SheetActionButtons(
                    uiState = uiState,
                    onAction = onAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            layoutDirection = LocalLayoutDirection.current,
                            horizontal = contentPadding
                        )
                        .padding(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding),
                )
                FileInfoList(
                    file = file,
                    modifier = Modifier.padding(
                        layoutDirection = LocalLayoutDirection.current,
                        horizontal = contentPadding
                    )
                        .padding(horizontal = 8.dp),
                )
                uiState.attribute?.let {
                    FileAttributeChips(
                        it,
                        modifier = Modifier
                            .padding(
                                layoutDirection = LocalLayoutDirection.current,
                                horizontal = contentPadding
                            )
                            .padding(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding),
                    )
                }
                Spacer(
                    modifier = Modifier.height(
                        contentPadding.calculateBottomPadding() + ExtraPaneScaffoldDefaults.HorizontalPadding
                    )
                )
            }
        }
    }
}

private fun Modifier.padding(
    layoutDirection: LayoutDirection,
    horizontal: PaddingValues,
): Modifier {
    val start: Dp
    val end: Dp
    with(layoutDirection) {
        start = horizontal.calculateStartPadding(this)
        end = horizontal.calculateEndPadding(this)
    }
    return this.padding(start = start, end = end)
}

@PreviewMultiScreen
@Composable
private fun PreviewFileInfoSheet(
    @PreviewParameter(FileInfoUiStateProvider::class) uiState: FileInfoUiState,
) {
    PreviewCanonicalScaffold(
        navigator = rememberSupportingPaneScaffoldNavigator(
            initialDestinationHistory = listOf(
                ThreePaneScaffoldDestinationItem(
                    SupportingPaneScaffoldRole.Extra,
                    uiState
                )
            )
        ),
        extraPane = { contentKey ->
            val screenState = rememberScrollState()
            val lazyPagingItems = PagingData.flowData(10) { BookThumbnail.from(fakeBookFile(it)) }
            FileInfoSheet(
                uiState = contentKey,
                onAction = {},
                scrollState = screenState,
                lazyPagingItems = lazyPagingItems.collectAsLazyPagingItems()
            )
        }
    ) {
    }
}

private class FileInfoUiStateProvider : PreviewParameterProvider<FileInfoUiState> {
    val attribute = FileAttribute(
        archive = true,
        compressed = true,
        hidden = true,
        normal = true,
        directory = true,
        readonly = true,
        sharedRead = true,
        system = true,
        temporary = true,
        volume = true,
    )
    override val values: Sequence<FileInfoUiState>
        get() = sequenceOf(
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(checked = false, loading = true),
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(checked = false, loading = false),
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(checked = true, loading = true),
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(checked = true, loading = false),
                isOpenFolderEnabled = true
            ),
        )
}
