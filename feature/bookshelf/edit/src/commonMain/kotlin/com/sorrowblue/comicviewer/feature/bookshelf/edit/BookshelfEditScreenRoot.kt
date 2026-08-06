/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey

@Composable
internal fun BookshelfEditScreenRoot(key: BookshelfWizardNavKey, onBack: () -> Unit) {
    val state = rememberBookshelfEditScreenState(key)
    BookshelfEditScreen(
        uiState = state.uiState,
        backStack = state.backStack,
        onBack = {
            if (!state.onBack()) {
                onBack()
            }
        },
        onComplete = onBack,
        onTypeClick = state::onSourceClick,
        updateCanSubmit = state::updateCanSubmit,
        discardConfirm = state::discardConfirm,
        onDismissRequest = {
            if (!state.onBack()) {
                onBack()
            }
        },
        onConfirm = { force ->
            if (force) {
                onBack()
            } else {
                state.onBack()
                if (!state.onBack()) {
                    onBack()
                }
            }
        },
    )
}
