/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditScreenState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SelectionList
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.rememberBookshelfEditScreenState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.wizard.BookshelfWizardPage
import com.sorrowblue.comicviewer.feature.bookshelf.edit.wizard.rememberBookshelfWizardScreenState
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_btn_confirm
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_btn_dismiss
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_discord_title
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_back
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_save
import comicviewer.feature.bookshelf.edit.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfEditScreenRoot(key: BookshelfWizardNavKey, onBack: () -> Unit) {
    val state = rememberBookshelfWizardScreenState(key)
    var editState: BookshelfEditScreenState? by mutableStateOf(null)
    BookshelfEditScreen(
        uiState = state.uiState,
        pages = state.pages,
        pagerState = state.pagerState,
        onBack = {
            if (!state.onBack()) {
                onBack()
            }
        },
        dismissButton = {
            if (state.pagerState.currentPage == 0) {
                TextButton(onClick = {
                    if (!state.onBack()) {
                        onBack()
                    }
                }) {
                    Text(stringResource(Res.string.cancel))
                }
            } else {
                TextButton(onClick = state::onPrevClick) {
                    Text(stringResource(Res.string.bookshelf_edit_label_back))
                }
            }
        },
        actionButton = {
            when (state.pages[state.pagerState.currentPage]) {
                is BookshelfWizardPage.Edit -> {
                    TextButton(
                        enabled = editState?.formState?.meta?.canSubmit == true,
                        onClick = {
                            editState?.form?.handleSubmit()
                        }
                    ) {
                        Text(stringResource(Res.string.bookshelf_edit_label_save))
                    }
                }

                BookshelfWizardPage.Selection -> Unit
            }
        },
        confirmButton = {
            when (state.pages[state.pagerState.currentPage]) {
                is BookshelfWizardPage.Edit -> {
                    FilledTonalButton(
                        enabled = editState?.formState?.meta?.canSubmit == true,
                        onClick = {
                            editState?.form?.handleSubmit()
                        }
                    ) {
                        Text(stringResource(Res.string.bookshelf_edit_label_save))
                    }
                }

                BookshelfWizardPage.Selection -> Unit
            }
        }
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
                    modifier = Modifier.fillMaxSize().testTag("BookshelfSelectionList"),
                )
            }

            is BookshelfWizardPage.Edit -> {
                editState = rememberBookshelfEditScreenState(editType = page.editType)
                BookshelfEditorContents(
                    state = editState!!,
                    contentPadding = contentPadding,
                    onBack = {
                        if (!state.onBack()) {
                            onBack()
                        }
                    },
                    onComplete = onBack,
                    onChange = {
                        state.onFormChange(it)
                    },
                    modifier = Modifier.fillMaxSize().testTag("BookshelfEditorContents"),
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
