package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.bookshelf.BookshelfConverter.source
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfInfoSheet(
    bookshelfFolder: BookshelfFolder,
    onDismissRequest: () -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(false)
) {
    val bookshelf = bookshelfFolder.bookshelf
    val folder = bookshelfFolder.folder
    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column(Modifier.padding(horizontal = 16.dp)) {
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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Button(onClick = onRemove) {
                    Text(text = stringResource(id = R.string.bookshelf_info_btn_remove))
                }
                FilledTonalButton(onClick = onEdit) {
                    Text(text = stringResource(id = R.string.bookshelf_info_btn_edit))
                }
            }
        }
    }

}
