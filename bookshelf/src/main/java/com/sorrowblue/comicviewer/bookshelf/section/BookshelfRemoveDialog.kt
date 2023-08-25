@file:OptIn(ExperimentalMaterial3Api::class)

package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun BookshelfRemoveDialog(title: String, onDismissRequest: () -> Unit, onRemove: () -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onRemove) {
                Text(stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = android.R.string.cancel))
            }
        },
        title = {
            Text("削除の確認")
        },
        text = {
            Text("$title を削除しますか？")
        })
}
