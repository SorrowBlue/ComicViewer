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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.file.component.FileAttributeChips
import com.sorrowblue.comicviewer.file.section.FileInfoList
import com.sorrowblue.comicviewer.file.section.FileInfoThumbnail
import com.sorrowblue.comicviewer.file.section.SheetActionButtons
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme2
import com.sorrowblue.comicviewer.framework.ui.preview.fakeBookFile
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReadLaterUiState(val checked: Boolean = false, val loading: Boolean = false) : Parcelable

@Parcelize
data class FileInfoUiState(
    val file: File,
    val attribute: FileAttribute? = null,
    val readLater: Boolean = false,
    val loading: Boolean = true,
    val readLaterUiState: ReadLaterUiState = ReadLaterUiState(),
    val isOpenFolderEnabled: Boolean = false,
) : Parcelable

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

@Composable
fun FileInfoSheet(
    file: File,
    onAction: (FileInfoSheetNavigator) -> Unit,
) {
    val state = rememberFileInfoSheetState2(file = file)
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
    CanonicalExtraPaneScaffold(
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
                    modifier = Modifier.padding(
                        layoutDirection = LocalLayoutDirection.current,
                        horizontal = contentPadding
                    ),
                )
                FileInfoList(file = file)
                uiState.attribute?.let {
                    FileAttributeChips(
                        it,
                        modifier = Modifier.padding(
                            layoutDirection = LocalLayoutDirection.current,
                            horizontal = contentPadding
                        )
                    )
                }
                Spacer(modifier = Modifier.size(contentPadding.calculateBottomPadding()))
            }
            if (scrollState.canScrollBackward) {
                HorizontalDivider(modifier = Modifier.padding(top = contentPadding.calculateTopPadding()))
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
    PreviewTheme2 {
        val screenState = rememberScrollState()
        CanonicalScaffold(
            navigator = rememberSupportingPaneScaffoldNavigator(
                initialDestinationHistory = listOf(
                    ThreePaneScaffoldDestinationItem(
                        SupportingPaneScaffoldRole.Extra,
                        uiState
                    )
                )
            ),
            extraPane = { content ->
                FileInfoSheet(
                    uiState = content,
                    onAction = {},
                    scrollState = screenState
                )
                LaunchedEffect(Unit) {
//                    screenState.scrollTo(screenState.maxValue)
                }
            }
        ) {
        }
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
                loading = true,
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(checked = true, loading = false),
                loading = true,
                isOpenFolderEnabled = true
            ),
        )
}
