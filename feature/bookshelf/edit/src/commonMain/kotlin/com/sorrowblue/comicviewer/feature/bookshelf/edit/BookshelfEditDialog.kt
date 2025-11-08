package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfSelectionDialog
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DiscardConfirmDialog
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
context(context: BookshelfEditScreenContext)
fun BookshelfEditDialog(state: BookshelfEditDialogState) {
    require(state is InternalBookshelfEditDialogState)
    when (val type = state.uiState.type) {
        is BookshelfEditDialogType.Editor ->
            BookshelfEditorDialog(
                type = type.editorType,
                onBackClick = state::hideDialog,
                discardConfirm = state::showDiscard,
                onEditComplete = state::hideDialog,
            )

        BookshelfEditDialogType.Selection ->
            BookshelfSelectionDialog(
                onBackClick = state::hideDialog,
                onTypeClick = state::showRegisterDialog,
            )

        BookshelfEditDialogType.Hide -> Unit
    }

    if (state.uiState.discard) {
        DiscardConfirmDialog(
            onBackClick = state::hideDiscard,
            onKeep = state::hideDiscard,
            onDiscard = state::hideDialog,
        )
    }
}
