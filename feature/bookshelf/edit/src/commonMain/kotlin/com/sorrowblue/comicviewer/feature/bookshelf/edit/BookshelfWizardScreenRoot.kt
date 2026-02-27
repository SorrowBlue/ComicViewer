package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SelectionList
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_btn_confirm
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_btn_dismiss
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_title
import org.jetbrains.compose.resources.stringResource

@Composable
context(context: BookshelfEditScreenContext)
internal fun BookshelfWizardScreenRoot(key: BookshelfWizardNavKey, onBack: () -> Unit) {
    val state = rememberBookshelfWizardScreenState(key)
    BookshelfWizardScreen(
        uiState = state.uiState,
        pages = state.pages,
        pagerState = state.pagerState,
        onBack = {
            if (!state.onBack()) {
                onBack()
            }
        },
    ) { page, contentPadding ->
        when (page) {
            is BookshelfWizardPage.Selection -> {
                val items = remember { BookshelfType.entries.toList() }
                SelectionList(
                    items = items,
                    onSourceClick = {
                        state.onSourceClick(it)
                    },
                    contentPadding = contentPadding.plus(PaddingValues(top = 16.dp)),
                    modifier = Modifier.testTag("BookshelfSelectionList"),
                )
            }

            is BookshelfWizardPage.Edit -> {
                BookshelfEditorContents(
                    page = page,
                    contentPadding = contentPadding,
                    onComplete = onBack,
                    onChange = {
                        state.onFormChange(it)
                    },
                    modifier = Modifier.testTag("BookshelfEditorContents"),
                )
            }
        }
    }
    AnimatedVisibility(state.showDiscardDialog) {
        AlertDialog(
            onDismissRequest = state::onKeep,
            confirmButton = {
                TextButton(onClick = onBack) {
                    Text(stringResource(Res.string.bookshelf_edit_discord_btn_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = state::onKeep) {
                    Text(stringResource(Res.string.bookshelf_edit_discord_btn_dismiss))
                }
            },
            text = {
                Text(stringResource(Res.string.bookshelf_edit_discord_title))
            },
        )
    }
}
