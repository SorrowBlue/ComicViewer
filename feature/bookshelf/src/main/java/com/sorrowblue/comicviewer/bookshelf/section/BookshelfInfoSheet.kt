package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.bookshelf.BookshelfConverter.source
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun BookshelfInfoSheet(
    bookshelfFolder: BookshelfFolder,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    onCloseClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    Row {
        Box(
            Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(color = DividerDefaults.color)
        )
        Column(
            Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(24.dp)
        ) {
            Row {
                Text(
                    text = "Bookshelf Info",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                )
                Spacer(modifier = Modifier.size(12.dp))
                IconButton(onClick = onCloseClick) {
                    Icon(imageVector = ComicIcons.Close, contentDescription = null)
                }
            }

            val bookshelf = bookshelfFolder.bookshelf
            val folder = bookshelfFolder.folder
            Text(
                text = stringResource(id = bookshelf.source()),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
            Text(
                text = bookshelf.displayName,
                style = MaterialTheme.typography.titleLarge
            )

            when (bookshelf) {
                is InternalStorage -> {
                    Text(
                        text = stringResource(id = R.string.bookshelf_info_label_path),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = folder.path,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is SmbServer -> {
                    Text(
                        text = stringResource(id = R.string.bookshelf_info_label_host),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = bookshelf.host,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(id = R.string.bookshelf_info_label_port),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = bookshelf.port.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(id = R.string.bookshelf_info_label_path),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = folder.path,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                TextButton(onClick = onRemove) {
                    Text(text = stringResource(id = R.string.bookshelf_info_btn_remove))
                }
                FilledTonalButton(onClick = onEdit) {
                    Text(text = stringResource(id = R.string.bookshelf_info_btn_edit))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBookshelfInfoSheet() {
    val bookshelfFolder = BookshelfFolder(
        SmbServer(
            BookshelfId(0),
            "displayName",
            "0.0.0.0",
            445,
            SmbServer.Auth.UsernamePassword("domain", "username", "password")
        ) to Folder(BookshelfId(0), "", "", "", 0, 0, emptyMap(), 0)
    )
    ComicTheme {
        PermanentDrawerSheet {
            BookshelfInfoSheet(bookshelfFolder, {}, {}, {}, PaddingValues())
        }
    }
}
