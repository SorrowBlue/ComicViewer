package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.bookshelf.BookshelfConverter.source
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder
import com.sorrowblue.comicviewer.framework.ui.responsive.BottomSheet
import com.sorrowblue.comicviewer.framework.ui.responsive.SideSheet
import com.sorrowblue.comicviewer.framework.ui.responsive.SideSheetDefault

@Composable
private fun ListItem(
    overline: String,
    headline: String,
) {
    val colors = ListItemDefaults.colors(
        containerColor = Color.Transparent
    )
    ListItem(
        colors = colors,
        headlineContent = { Text(text = headline) },
        overlineContent = { Text(text = overline) }
    )
}

@Composable
internal fun BookshelfBottomSheet(
    bookshelfFolder: BookshelfFolder,
    onDismissRequest: () -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    onScanClick: () -> Unit,
) {
    val bookshelf = bookshelfFolder.bookshelf
    val folder = bookshelfFolder.folder
    BottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            Modifier
                .padding(horizontal = ComicTheme.dimension.margin)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = folder,
                    contentDescription = null,
                    placeholder = rememberDebugPlaceholder(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(128.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ComicTheme.colorScheme.surfaceContainerLowest)
                )
                AssistChip(
                    onClick = { },
                    label = { Text(text = stringResource(id = bookshelf.source())) },
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding * 2))
            ListItem(
                headline = bookshelf.displayName,
                overline = stringResource(id = R.string.bookshelf_info_label_display_name)
            )

            when (bookshelf) {
                is InternalStorage -> {
                    ListItem(
                        headline = folder.path,
                        overline = stringResource(id = R.string.bookshelf_info_label_path)
                    )
                }

                is SmbServer -> {
                    ListItem(
                        headline = bookshelf.host,
                        overline = stringResource(id = R.string.bookshelf_info_label_host)
                    )
                    ListItem(
                        headline = bookshelf.port.toString(),
                        overline = stringResource(id = R.string.bookshelf_info_label_port)
                    )
                    ListItem(
                        headline = folder.path,
                        overline = stringResource(id = R.string.bookshelf_info_label_path)
                    )
                }
            }
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding * 2))

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onRemove) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = ComicIcons.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.bookshelf_action_delete))
                    }
                }
                TextButton(onClick = onEdit) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = ComicIcons.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.bookshelf_action_edit))
                    }
                }
                TextButton(onClick = onScanClick) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = ComicIcons.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = R.string.bookshelf_action_scan))
                    }
                }
            }
        }
    }
}

@Composable
fun BookshelfSideSheet(
    bookshelfFolder: BookshelfFolder,
    onRemoveClick: () -> Unit,
    onEditClick: () -> Unit,
    onScanClick: () -> Unit,
    onCloseClick: () -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    SideSheet(
        title = stringResource(id = R.string.bookshelf_info_title),
        width = SideSheetDefault.MaxWidth,
        innerPadding = innerPadding,
        onCloseClick = onCloseClick,
        modifier = modifier
    ) {
        val bookshelf = bookshelfFolder.bookshelf
        val folder = bookshelfFolder.folder

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = folder,
                contentDescription = null,
                placeholder = rememberDebugPlaceholder(),
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ComicTheme.colorScheme.surfaceContainerHigh),
                contentScale = ContentScale.Crop
            )
            AssistChip(
                onClick = { },
                label = { Text(text = stringResource(id = bookshelf.source())) },
            )
        }
        ListItem(
            headline = bookshelf.displayName,
            overline = stringResource(id = R.string.bookshelf_info_label_display_name)
        )

        when (bookshelf) {
            is InternalStorage -> {
                ListItem(
                    headline = folder.path,
                    overline = stringResource(id = R.string.bookshelf_info_label_path)
                )
            }

            is SmbServer -> {
                ListItem(
                    headline = bookshelf.host,
                    overline = stringResource(id = R.string.bookshelf_info_label_host)
                )
                ListItem(
                    headline = bookshelf.port.toString(),
                    overline = stringResource(id = R.string.bookshelf_info_label_port)
                )
                ListItem(
                    headline = folder.path,
                    overline = stringResource(id = R.string.bookshelf_info_label_path)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onRemoveClick) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ComicIcons.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.bookshelf_action_delete))
                }
            }
            TextButton(onClick = onEditClick) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ComicIcons.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.bookshelf_action_edit))
                }
            }
            TextButton(onClick = onScanClick) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ComicIcons.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.bookshelf_action_scan))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBookshelfBottomSheet() {
    val bookshelfFolder = BookshelfFolder(
        SmbServer(
            BookshelfId(0),
            "displayName",
            "0.0.0.0",
            445,
            SmbServer.Auth.UsernamePassword("domain", "username", "password")
        ),
        Folder(BookshelfId(0), "", "", "/path/sample/", 0, 0, emptyMap(), 0)
    )
    PreviewTheme {
        Surface(color = ComicTheme.colorScheme.surfaceContainerLow) {
            BookshelfBottomSheet(bookshelfFolder, {}, {}, {}, {})
        }
    }
}

@Preview
@Composable
private fun PreviewBookshelfSideSheet() {
    val bookshelfFolder = BookshelfFolder(
        SmbServer(
            BookshelfId(0),
            "displayName",
            "0.0.0.0",
            445,
            SmbServer.Auth.UsernamePassword("domain", "username", "password")
        ),
        Folder(BookshelfId(0), "", "", "/path/sample/", 0, 0, emptyMap(), 0)
    )
    PreviewTheme {
        Surface(color = ComicTheme.colorScheme.surfaceContainerLow) {
            BookshelfSideSheet(
                innerPadding = PaddingValues(),
                bookshelfFolder = bookshelfFolder,
                onRemoveClick = { /*TODO*/ },
                onEditClick = { /*TODO*/ },
                onScanClick = {},
                onCloseClick = {}
            )
        }
    }
}
