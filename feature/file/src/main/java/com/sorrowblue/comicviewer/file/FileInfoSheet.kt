package com.sorrowblue.comicviewer.file

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.model.file.fakeBookFile
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AsyncImage2
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffoldDefault
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileInfoUiState(
    val file: File,
    val attribute: FileAttribute? = null,
    val isReadLater: Boolean = false,
    val loading: Boolean = true,
) : Parcelable

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <T> rememberThreePaneScaffoldNavigatorContent(
    navigator: ThreePaneScaffoldNavigator<T>,
): MutableState<T?> = remember(navigator.currentDestination?.content) {
    mutableStateOf(navigator.currentDestination?.content)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun FileInfoSheet(
    file: File,
    onCloseClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    modifier: Modifier = Modifier,
    isReadLater: Boolean = false,
    fileAttribute: FileAttribute? = null,
    onOpenFolderClick: (() -> Unit)? = null,
) {
    FileInfoSheet(
        fileInfoUiState = FileInfoUiState(file, fileAttribute, isReadLater),
        onCloseClick = onCloseClick,
        onReadLaterClick = onReadLaterClick,
        onFavoriteClick = onFavoriteClick,
        contentPadding = contentPadding,
        scaffoldDirective = scaffoldDirective,
        modifier = modifier,
        onOpenFolderClick = onOpenFolderClick
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun FileInfoSheet(
    fileInfoUiState: FileInfoUiState,
    onCloseClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    modifier: Modifier = Modifier,
    onOpenFolderClick: (() -> Unit)? = null,
) {
    val file = fileInfoUiState.file
    val fileAttribute = fileInfoUiState.attribute
    ExtraPaneScaffold(
        topBar = {
            ExtraPaneScaffoldDefault.TopAppBar(
                title = { Text(text = file.name) },
                onCloseClick = onCloseClick,
                scaffoldDirective = scaffoldDirective
            )
        },
        contentPadding = contentPadding,
        scaffoldDirective = scaffoldDirective,
        modifier = modifier
    ) {
        AsyncImage2(
            model = file,
            contentDescription = null,
            placeholder = rememberDebugPlaceholder(),
            error = {
                Icon(
                    imageVector = if (file is Book) ComicIcons.Book else ComicIcons.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
            },
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(16.dp * 12)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        if (fileInfoUiState.isReadLater) {
            FilledTonalButton(
                onClick = onReadLaterClick,
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
                onClick = onReadLaterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ComicTheme.dimension.minPadding * 4)
                    .padding(horizontal = ComicTheme.dimension.minPadding * 4)
            ) {
                if (fileInfoUiState.loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Icon(imageVector = ComicIcons.WatchLater, contentDescription = null)
                }
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = R.string.file_info_label_add_read_later))
            }
        }
        OutlinedButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ComicTheme.dimension.minPadding * 4)
        ) {
            Icon(imageVector = ComicIcons.Favorite, contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.file_info_label_add_favourites))
        }
        if (onOpenFolderClick != null) {
            OutlinedButton(
                onClick = onOpenFolderClick,
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
                        text = stringResource(
                            id = R.string.file_text_page_count,
                            file.lastPageRead,
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Preview
@Composable
private fun PreviewFileInfoSheet() {
    PreviewTheme {
        FileInfoSheet(
            file = fakeBookFile(),
            fileAttribute = FileAttribute(
                archive = true,
                compressed = true,
                directory = true,
                normal = true,
                readonly = true,
                system = true,
                temporary = true,
                sharedRead = true,
                hidden = true,
                volume = true
            ),
            onCloseClick = { /*TODO*/ },
            onReadLaterClick = { /*TODO*/ },
            onFavoriteClick = { /*TODO*/ },
            onOpenFolderClick = {},
            contentPadding = PaddingValues(),
            scaffoldDirective = rememberSupportingPaneScaffoldNavigator<Pair<File, FileAttribute?>>().scaffoldDirective
        )
    }
}
