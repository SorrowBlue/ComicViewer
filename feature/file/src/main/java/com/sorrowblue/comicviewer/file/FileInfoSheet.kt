package com.sorrowblue.comicviewer.file

import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.previewPainter
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileInfoUiState(
    val file: File,
    val attribute: FileAttribute? = null,
    val isReadLater: Boolean = false,
    val loading: Boolean = true,
    val isOpenFolderEnabled: Boolean = false,
) : Parcelable

sealed interface FileInfoSheetAction {
    data object Close : FileInfoSheetAction
    data object ReadLater : FileInfoSheetAction
    data object Favorite : FileInfoSheetAction
    data object OpenFolder : FileInfoSheetAction
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FileInfoSheet(
    uiState: FileInfoUiState,
    onAction: (FileInfoSheetAction) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val file = uiState.file
    val fileAttribute = uiState.attribute
    CanonicalExtraPaneScaffold(
        title = {
            Text(text = file.name)
        },
        onCloseClick = { onAction(FileInfoSheetAction.Close) },
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        val screenState = rememberScrollState()
        Column(
            Modifier
                .fillMaxSize()
                .drawVerticalScrollbar(screenState)
                .verticalScroll(screenState)
                .padding(it)
        ) {
            SubcomposeAsyncImage(
                model = file,
                contentDescription = null,
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .wrapContentSize()
                            .align(Alignment.Center)
                    )
                },
                error = {
                    if (LocalInspectionMode.current) {
                        Image(
                            painter = previewPainter(),
                            contentDescription = null,
                            contentScale = contentScale
                        )
                    } else {
                        Icon(
                            imageVector = if (file is Book) ComicIcons.BrokenImage else ComicIcons.FolderOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .wrapContentSize()
                                .sizeIn(minHeight = 48.dp, minWidth = 48.dp)
                                .align(Alignment.Center)
                        )
                    }
                },
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(16.dp * 12)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            if (uiState.isReadLater) {
                FilledTonalButton(
                    onClick = { onAction(FileInfoSheetAction.ReadLater) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = ComicTheme.dimension.minPadding * 4)
                        .padding(horizontal = ComicTheme.dimension.minPadding * 4)
                ) {
                    Icon(imageVector = ComicIcons.WatchLater, contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.file_info_label_add_read_later))
                }
            } else {
                OutlinedButton(
                    onClick = { onAction(FileInfoSheetAction.ReadLater) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = ComicTheme.dimension.minPadding * 4)
                        .padding(horizontal = ComicTheme.dimension.minPadding * 4)
                ) {
                    if (uiState.loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Icon(imageVector = ComicIcons.WatchLater, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.file_info_label_add_read_later))
                }
            }
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
            ListItem(
                overlineContent = { Text(text = "パス") },
                headlineContent = { Text(text = file.path) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            ListItem(
                overlineContent = { Text(text = "種類") },
                headlineContent = { Text(text = if (file is IFolder) "フォルダ" else file.name.extension) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            ListItem(
                overlineContent = { Text(text = "サイズ") },
                headlineContent = { Text(text = file.size.asFileSize) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            ListItem(
                overlineContent = { Text(text = stringResource(R.string.file_label_modified_date)) },
                headlineContent = { Text(text = file.lastModifier.asDateTime) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
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
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                ListItem(
                    overlineContent = { Text(text = "最後に読んだ日時") },
                    headlineContent = {
                        Text(
                            text = file.lastReadTime.asDateTime
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }

            Text(
                text = "属性",
                style = ComicTheme.typography.labelSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ComicTheme.dimension.minPadding * 4)
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ComicTheme.dimension.minPadding * 4),
                horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.minPadding)
            ) {
                fileAttribute?.let {
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
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun PreviewFileInfoSheet() {
    PreviewTheme {
        FileInfoSheet(
            uiState = FileInfoUiState(fakeBookFile()),
            onAction = {},
            contentPadding = PaddingValues()
        )
    }
}
