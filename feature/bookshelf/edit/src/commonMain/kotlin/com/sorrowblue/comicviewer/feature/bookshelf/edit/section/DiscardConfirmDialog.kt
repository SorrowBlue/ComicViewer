package com.sorrowblue.comicviewer.feature.bookshelf.edit.section

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_btn_confirm
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_btn_dismiss
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DiscardConfirmDialog(
    onBackClick: () -> Unit,
    onDiscard: () -> Unit,
    onKeep: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onBackClick,
        confirmButton = {
            TextButton(onClick = onDiscard) {
                Text(stringResource(Res.string.bookshelf_edit_discord_btn_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onKeep) {
                Text(stringResource(Res.string.bookshelf_edit_discord_btn_dismiss))
            }
        },
        text = {
            Text(stringResource(Res.string.bookshelf_edit_discord_title))
        }
    )
}
