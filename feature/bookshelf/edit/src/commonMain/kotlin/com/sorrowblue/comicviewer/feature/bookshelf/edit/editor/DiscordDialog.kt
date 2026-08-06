/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.editor

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditPage
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_btn_confirm
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_btn_dismiss
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_title
import org.jetbrains.compose.resources.stringResource

internal fun EntryProviderScope<NavKey>.discordDialogEntry(
    onDismissRequest: () -> Unit,
    onConfirm: (Boolean) -> Unit,
) {
    entry<BookshelfEditPage.Discard>(metadata = DialogSceneStrategy.dialog()) { key ->
        DiscordDialog(
            onDismissRequest = onDismissRequest,
            onConfirm = { onConfirm(key.force) },
        )
    }
}

@Composable
private fun DiscordDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm, modifier = Modifier.testTag("ConfirmButton")) {
                Text(stringResource(Res.string.bookshelf_edit_discord_btn_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest, modifier = Modifier.testTag("DismissButton")) {
                Text(stringResource(Res.string.bookshelf_edit_discord_btn_dismiss))
            }
        },
        text = {
            Text(stringResource(Res.string.bookshelf_edit_discord_title))
        },
        modifier = Modifier.testTag("DiscordDialog"),
    )
}
