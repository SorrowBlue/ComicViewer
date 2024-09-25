package com.sorrowblue.comicviewer.file

import android.os.Parcelable
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.file.component.BookThumbnailImage
import com.sorrowblue.comicviewer.file.component.FolderThumbnailsCarousel
import com.sorrowblue.comicviewer.file.component.ReadlaterButton
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
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

sealed interface FileInfoSheetAction {
    data object Close : FileInfoSheetAction
    data object ReadLater : FileInfoSheetAction
    data object Favorite : FileInfoSheetAction
    data object OpenFolder : FileInfoSheetAction
}

@Composable
fun FileInfoSheet(
    file: File,
    onAction: (FileInfoSheetAction) -> Unit,
) {
    val state = rememberFileInfoSheetState2(file = file)
    FileInfoSheet(
        uiState = state.uiState,
        lazyPagingItems = state.lazyPagingItems,
        onAction = state::onAction,
    )

    LaunchedEventEffect(state.event) {
        when (it) {
            FileInfoSheetStateEvent.Close -> onAction(FileInfoSheetAction.Close)
            is FileInfoSheetStateEvent.Favorite -> onAction(FileInfoSheetAction.Favorite)
            is FileInfoSheetStateEvent.OpenFolder -> onAction(FileInfoSheetAction.OpenFolder)
        }
    }
}

@Composable
fun FileInfoSheet(
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
                if (lazyPagingItems != null) {
                    FolderThumbnailsCarousel(
                        lazyPagingItems = lazyPagingItems,
                    )
                } else {
                    BookThumbnailImage(
                        thumbnail = FileThumbnail.from(file),
                        modifier = Modifier
                            .size(186.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                SheetActionButtons(
                    uiState = uiState,
                    onAction = onAction,
                    modifier = Modifier.padding(horizontal = contentPadding),
                )
                FileInfoList(file = file)
                uiState.attribute?.let {
                    FileAttributeChips(
                        it,
                        modifier = Modifier.padding(horizontal = contentPadding)
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

@Composable
private fun Modifier.padding(horizontal: PaddingValues): Modifier {
    val start: Dp
    val end: Dp
    with(LocalLayoutDirection.current) {
        start = horizontal.calculateStartPadding(this)
        end = horizontal.calculateEndPadding(this)
    }
    return padding(start = start, end = end)
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FileAttributeChips(fileAttribute: FileAttribute, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "属性",
            style = ComicTheme.typography.labelSmall,
            modifier = Modifier
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.minPadding)
        ) {
            fileAttribute.let {
                if (it.archive) {
                    AssistChip(onClick = {}, label = { Text(text = "アーカイブ") })
                }
                if (it.compressed) {
                    AssistChip(onClick = {}, label = { Text(text = "圧縮") })
                }
                if (it.hidden) {
                    AssistChip(onClick = {}, label = { Text(text = "隠しファイル") })
                }
                if (it.normal) {
                    AssistChip(onClick = {}, label = { Text(text = "標準") })
                }
                if (it.directory) {
                    AssistChip(onClick = {}, label = { Text(text = "ディレクトリ") })
                }
                if (it.readonly) {
                    AssistChip(onClick = {}, label = { Text(text = "読取専用") })
                }
                if (it.sharedRead) {
                    AssistChip(onClick = {}, label = { Text(text = "読取共有アクセス") })
                }
                if (it.system) {
                    AssistChip(onClick = {}, label = { Text(text = "システム") })
                }
                if (it.temporary) {
                    AssistChip(onClick = {}, label = { Text(text = "一時ファイル") })
                }
                if (it.volume) {
                    AssistChip(onClick = {}, label = { Text(text = "ボリューム") })
                }
            }
        }
    }
}

@Composable
fun FileInfoList(file: File) {
    val transparentColor = ListItemDefaults.colors(containerColor = Color.Transparent)
    ListItem(
        overlineContent = { Text(text = "パス") },
        headlineContent = { Text(text = file.path) },
        colors = transparentColor
    )
    ListItem(
        overlineContent = { Text(text = "種類") },
        headlineContent = { Text(text = if (file is IFolder) "フォルダ" else file.name.extension) },
        colors = transparentColor
    )
    ListItem(
        overlineContent = { Text(text = "サイズ") },
        headlineContent = { Text(text = file.size.asFileSize) },
        colors = transparentColor
    )
    ListItem(
        overlineContent = { Text(text = stringResource(R.string.file_label_modified_date)) },
        headlineContent = { Text(text = file.lastModifier.asDateTime) },
        colors = transparentColor
    )
    if (file is Book) {
        ListItem(
            overlineContent = { Text(text = "ページ数") },
            headlineContent = {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.file_text_page_count,
                        count = file.totalPageCount,
                        file.totalPageCount
                    )
                )
            },
            colors = transparentColor
        )
        ListItem(
            overlineContent = { Text(text = "最後に読んだ日時") },
            headlineContent = { Text(text = file.lastReadTime.asDateTime) },
            colors = transparentColor
        )
    }
}

@Composable
private fun SheetActionButtons(
    uiState: FileInfoUiState,
    onAction: (FileInfoSheetAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ReadlaterButton(
            uiState = uiState.readLaterUiState,
            onClick = { onAction(FileInfoSheetAction.ReadLater) })
        OutlinedButton(
            onClick = { onAction(FileInfoSheetAction.Favorite) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ComicTheme.dimension.minPadding * 4)
        ) {
            Icon(imageVector = ComicIcons.Favorite, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.file_info_label_add_favourites))
        }
        if (uiState.isOpenFolderEnabled) {
            OutlinedButton(
                onClick = { onAction(FileInfoSheetAction.OpenFolder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ComicTheme.dimension.minPadding * 4)
            ) {
                Icon(imageVector = ComicIcons.FolderOpen, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = R.string.file_info_label_open_folder))
            }
        }
    }
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
                readLaterUiState = ReadLaterUiState(false, true),
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(false, false),
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(true, true),
                loading = true,
                isOpenFolderEnabled = true
            ),
            FileInfoUiState(
                fakeBookFile(),
                attribute = attribute,
                readLaterUiState = ReadLaterUiState(true, false),
                loading = true,
                isOpenFolderEnabled = true
            ),
        )
}
