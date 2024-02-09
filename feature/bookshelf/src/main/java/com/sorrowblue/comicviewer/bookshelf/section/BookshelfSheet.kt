package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.PaneScaffoldDirective
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfConverter.source
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.DocumentUnknown
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AsyncImage2
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffoldDefault
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalLayoutApi::class)
@Composable
fun BookshelfInfoSheet(
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    bookshelfFolder: BookshelfFolder,
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
    onScanClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtraPaneScaffold(
        topBar = {
            ExtraPaneScaffoldDefault.TopAppBar(
                title = { Text(text = stringResource(id = R.string.bookshelf_info_title)) },
                onCloseClick = onCloseClick,
                scaffoldDirective = scaffoldDirective
            )
        },
        modifier = modifier,
        contentPadding = contentPadding,
        scaffoldDirective = scaffoldDirective
    ) {
        val bookshelf = bookshelfFolder.bookshelf
        val folder = bookshelfFolder.folder
        val colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        AsyncImage2(
            model = folder,
            contentDescription = stringResource(id = R.string.bookshelf_desc_thumbnail),
            placeholder = rememberDebugPlaceholder(),
            modifier = Modifier
                .aspectRatio(16f / 9f)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(16.dp))
                .background(ComicTheme.colorScheme.surfaceContainerHighest),
            contentScale = ContentScale.Crop,
            error = {
                Icon(imageVector = ComicIcons.Image, contentDescription = null)
            },
            loading = {
                Icon(imageVector = ComicIcons.DocumentUnknown, contentDescription = null)
            }
        )
        ListItem(
            colors = colors,
            overlineContent = { Text(text = "種類") },
            headlineContent = { Text(text = stringResource(id = bookshelf.source())) },
        )
        ListItem(
            colors = colors,
            overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_display_name)) },
            headlineContent = { Text(text = bookshelf.displayName) },
        )

        when (bookshelf) {
            is InternalStorage -> {
                ListItem(
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) },
                    headlineContent = { Text(text = folder.path) },
                )
            }

            is SmbServer -> {
                ListItem(
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_host)) },
                    headlineContent = { Text(text = bookshelf.host) },
                )
                ListItem(
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_port)) },
                    headlineContent = { Text(text = bookshelf.port.toString()) },
                )
                ListItem(
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) },
                    headlineContent = { Text(text = folder.path) },
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            AssistChip(
                onClick = onRemoveClick,
                label = { Text(text = stringResource(id = R.string.bookshelf_action_delete)) },
                leadingIcon = { Icon(imageVector = ComicIcons.Delete, contentDescription = null) }
            )
            AssistChip(
                onClick = onEditClick,
                label = { Text(text = stringResource(id = R.string.bookshelf_action_edit)) },
                leadingIcon = { Icon(imageVector = ComicIcons.Edit, contentDescription = null) }
            )
            AssistChip(
                onClick = onScanClick,
                label = { Text(text = stringResource(id = R.string.bookshelf_action_scan)) },
                leadingIcon = { Icon(imageVector = ComicIcons.Refresh, contentDescription = null) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
private fun PreviewBookshelfInfoSheet() {
    PreviewTheme {
        BookshelfInfoSheet(
            contentPadding = PaddingValues(),
            bookshelfFolder = BookshelfFolder(
                SmbServer("DisplayName", "127.0.0.1", 455, SmbServer.Auth.Guest),
                Folder(bookshelfId = BookshelfId(0), "DisplayName", "", "/comic/test/test1", 0, 0)
            ),
            scaffoldDirective = rememberSupportingPaneScaffoldNavigator<BookshelfFolder>().scaffoldState.scaffoldDirective,
            onCloseClick = {},
            onEditClick = {},
            onRemoveClick = {},
            onScanClick = {}
        )
    }
}
