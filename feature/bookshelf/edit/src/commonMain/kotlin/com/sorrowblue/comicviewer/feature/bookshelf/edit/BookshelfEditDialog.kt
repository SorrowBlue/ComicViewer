package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface BookshelfEditDialogType {
    @Serializable
    data object Selection : BookshelfEditDialogType

    @Serializable
    data class Editor(val editorType: BookshelfEditorType) : BookshelfEditDialogType

    @Serializable
    data object Hide : BookshelfEditDialogType
}

@Serializable
internal data class BookshelfEditDialogUiState(
    val type: BookshelfEditDialogType = BookshelfEditDialogType.Hide,
    val discard: Boolean = false,
)

@Composable
internal fun BookshelfEditDialog(state: InternalBookshelfEditDialogState) {
    when (val type = state.uiState.type) {
        is BookshelfEditDialogType.Editor ->
            BookshelfEditorScreen(
                type = type.editorType,
                onNavigateUp = {
                    if (it) {
                        state.showDiscard()
                    } else {
                        state.hideDialog()
                    }
                },
                onEditComplete = state::hideDialog
            )

        BookshelfEditDialogType.Selection ->
            BookshelfSelectionScreen(
                onNavigateUp = state::hideDialog,
                onTypeClick = state::showRegisterDialog
            )

        BookshelfEditDialogType.Hide -> Unit
    }

    if (state.uiState.discard) {
        DiscardConfirmDialog(
            onDismissRequest = state::hideDiscard,
            onKeep = state::hideDiscard,
            onDiscard = state::hideDialog,
        )
    }
}
