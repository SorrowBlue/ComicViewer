/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.feature.bookshelf.nav.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.framework.ui.EventEffect

@Composable
internal fun BookshelfEditScreenRoot(
    key: BookshelfWizardNavKey,
    onBack: () -> Unit,
    showLocalNetworkPermissionRationale: () -> Unit = {},
) {
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

    EventEffect(state.events) {
        when (it) {
            BookshelfEditScreenRootEvent.ShowLocalNetworkPermissionRationale ->
                showLocalNetworkPermissionRationale()
        }
    }
}
