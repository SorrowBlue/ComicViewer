package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.bookshelf.R

@Composable
internal fun BookshelfRemoveDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.bookshelf_edit_title_remove))
        },
        text = {
            Text(text = stringResource(id = R.string.bookshelf_remove_label, title))
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(stringResource(id = R.string.bookshelf_remove_label_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = android.R.string.cancel))
            }
        }
    )
}
