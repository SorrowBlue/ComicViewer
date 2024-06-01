package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.ButtonWithIconContentPadding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
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
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.fakeFolder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.DocumentUnknown
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AsyncImage2
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffold
import com.sorrowblue.comicviewer.framework.ui.ExtraPaneScaffoldDefault
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookshelfInfoSheet(
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    bookshelfFolder: BookshelfFolder,
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
    onScanClick: () -> Unit,
    onReThumbnailsClick: () -> Unit,
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
                .padding(horizontal = ComicTheme.dimension.margin)
                .height(120.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(16.dp))
                .background(ComicTheme.colorScheme.surfaceContainerHighest),
            contentScale = ContentScale.Fit,
            error = {
                Icon(imageVector = ComicIcons.Image, contentDescription = null)
            },
            loading = {
                Icon(imageVector = ComicIcons.DocumentUnknown, contentDescription = null)
            }
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 8.dp)
        ) {
            AssistChip(
                onClick = onScanClick,
                label = { Text(text = stringResource(id = R.string.bookshelf_action_scan)) },
                leadingIcon = { Icon(imageVector = ComicIcons.Refresh, contentDescription = null) }
            )
            AssistChip(
                onClick = onReThumbnailsClick,
                label = { Text(text = stringResource(id = R.string.bookshelf_action_scan) + "サムネイル") },
                leadingIcon = { Icon(imageVector = ComicIcons.Refresh, contentDescription = null) }
            )
        }
        ListItem(
            colors = colors,
            modifier = Modifier.height(56.dp),
            overlineContent = { Text(text = "種類") },
            headlineContent = { Text(text = stringResource(id = bookshelf.source())) },
        )
        ListItem(
            modifier = Modifier.height(56.dp),
            colors = colors,
            overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_display_name)) },
            headlineContent = { Text(text = bookshelf.displayName) },
        )

        when (bookshelf) {
            is InternalStorage -> {
                ListItem(
                    modifier = Modifier.height(56.dp),
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) },
                    headlineContent = { Text(text = folder.path) },
                )
            }

            is SmbServer -> {
                ListItem(
                    modifier = Modifier.height(56.dp),
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_host)) },
                    headlineContent = { Text(text = bookshelf.host) },
                )
                ListItem(
                    modifier = Modifier.height(56.dp),
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_port)) },
                    headlineContent = { Text(text = bookshelf.port.toString()) },
                )
                ListItem(
                    modifier = Modifier.height(56.dp),
                    colors = colors,
                    overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) },
                    headlineContent = { Text(text = folder.path) },
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ComicTheme.dimension.margin)
        ) {
            TextButton(onClick = onRemoveClick) {
                Text(text = stringResource(id = R.string.bookshelf_action_delete))
            }
            FilledTonalButton(
                onClick = onEditClick,
                contentPadding = ButtonWithIconContentPadding
            ) {
                Icon(imageVector = ComicIcons.Edit, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = R.string.bookshelf_action_edit))
            }
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
                fakeFolder()
            ),
            scaffoldDirective = rememberSupportingPaneScaffoldNavigator<BookshelfFolder>().scaffoldDirective,
            onCloseClick = {},
            onEditClick = {},
            onRemoveClick = {},
            onScanClick = {},
            onReThumbnailsClick = {}
        )
    }
}
