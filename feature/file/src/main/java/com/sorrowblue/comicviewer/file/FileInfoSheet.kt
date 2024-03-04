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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.extension
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.model.file.fakeBookFile
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.file.component.forwardingPainter
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffoldDefault
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileInfo(
    val file: File,
    val attribute: FileAttribute? = null,
    val isReadLater: Boolean = false,
) : Parcelable

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun FileInfoSheet(
    file: File,
    fileAttribute: FileAttribute? = null,
    isReadLater: Boolean = false,
    onCloseClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    modifier: Modifier = Modifier,
    onOpenFolderClick: (() -> Unit)? = null,
) {
    FileInfoSheet(
        fileInfo = FileInfo(file, fileAttribute, isReadLater),
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
    fileInfo: FileInfo,
    onCloseClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    modifier: Modifier = Modifier,
    onOpenFolderClick: (() -> Unit)? = null,
) {
    val file = fileInfo.file
    val fileAttribute = fileInfo.attribute
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
        AsyncImage(
            model = file,
            contentDescription = null,
            placeholder = rememberDebugPlaceholder(),
            error = forwardingPainter(
                rememberVectorPainter(if (file is Book) ComicIcons.Book else ComicIcons.Folder),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface)
            ),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(16.dp * 12)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        if (fileInfo.isReadLater) {
            FilledTonalButton(
                onClick = onReadLaterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ComicTheme.dimension.padding * 4)
                    .padding(horizontal = ComicTheme.dimension.padding * 4)
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
                    .padding(top = ComicTheme.dimension.padding * 4)
                    .padding(horizontal = ComicTheme.dimension.padding * 4)
            ) {
                Icon(imageVector = ComicIcons.WatchLater, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = R.string.file_info_label_add_read_later))
            }
        }
        OutlinedButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ComicTheme.dimension.padding * 4)
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
                    .padding(horizontal = ComicTheme.dimension.padding * 4)
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
                .padding(horizontal = ComicTheme.dimension.padding * 4)
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ComicTheme.dimension.padding * 4),
            horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding)
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
fun PreviewFileInfoSheet() {
    PreviewTheme {
        FileInfoSheet(
            file = fakeBookFile(),
            fileAttribute = FileAttribute(
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true
            ),
            onCloseClick = { /*TODO*/ },
            onReadLaterClick = { /*TODO*/ },
            onFavoriteClick = { /*TODO*/ },
            onOpenFolderClick = {},
            contentPadding = PaddingValues(),
            scaffoldDirective = rememberSupportingPaneScaffoldNavigator<Pair<File, FileAttribute?>>().scaffoldState.scaffoldDirective
        )

    }
}
