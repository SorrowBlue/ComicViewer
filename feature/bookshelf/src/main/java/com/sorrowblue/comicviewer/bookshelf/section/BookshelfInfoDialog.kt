package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.debugPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookshelfInfoDialog(
    bookshelfFolder: BookshelfFolder,
    onDismissRequest: () -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(true),
        windowInsets = WindowInsets(0)
    ) {
        val bookshelf = bookshelfFolder.bookshelf
        val folder = bookshelfFolder.folder
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.bookshelf_info_title),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.size(16.dp))

            AsyncImage(
                model = folder,
                contentDescription = null,
                placeholder = debugPlaceholder(),
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.size(16.dp))
            AssistChip(
                onClick = { },
                label = { Text(text = stringResource(id = bookshelf.source())) },
            )
            ListItem(
                headlineContent = { Text(text = bookshelf.displayName) },
                overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_display_name)) }
            )

            when (bookshelf) {
                is InternalStorage -> {
                    ListItem(
                        headlineContent = { Text(text = folder.path) },
                        overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) }
                    )
                }

                is SmbServer -> {
                    ListItem(
                        headlineContent = { Text(text = bookshelf.host) },
                        overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_host)) }
                    )
                    ListItem(
                        headlineContent = { Text(text = bookshelf.port.toString()) },
                        overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_port)) }
                    )
                    ListItem(
                        headlineContent = { Text(text = folder.path) },
                        overlineContent = { Text(text = stringResource(id = R.string.bookshelf_info_label_path)) }
                    )
                }
            }
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
private fun PreviewBookshelfInfoDialog() {
    val bookshelfFolder = BookshelfFolder(
        SmbServer(
            BookshelfId(0),
            "displayName",
            "0.0.0.0",
            445,
            SmbServer.Auth.UsernamePassword("domain", "username", "password")
        ) to Folder(BookshelfId(0), "", "", "/path/sample/", 0, 0, emptyMap(), 0)
    )
    ComicTheme {
        BookshelfInfoDialog(bookshelfFolder, {}, {}, {})
    }
}
