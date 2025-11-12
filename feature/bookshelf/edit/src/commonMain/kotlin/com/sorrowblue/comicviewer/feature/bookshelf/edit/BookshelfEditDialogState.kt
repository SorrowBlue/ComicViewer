package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver

interface BookshelfEditDialogState {
    fun showSelectionDialog()

    fun showEditorDialog(bookshelfId: BookshelfId, bookshelfType: BookshelfType)
}

internal interface InternalBookshelfEditDialogState : BookshelfEditDialogState {
    val uiState: BookshelfEditDialogUiState

    fun showRegisterDialog(bookshelfType: BookshelfType)

    fun showDiscard()

    fun hideDiscard()

    fun hideDialog()
}

@Composable
fun rememberBookshelfEditDialogState(): BookshelfEditDialogState =
    rememberSaveable(saver = BookshelfEditDialogStateImpl.Saver) {
        BookshelfEditDialogStateImpl()
    }

private class BookshelfEditDialogStateImpl : InternalBookshelfEditDialogState {
    companion object {
        val Saver = kSerializableSaver<BookshelfEditDialogStateImpl, BookshelfEditDialogUiState>(
            save = { it.uiState },
            restore = { BookshelfEditDialogStateImpl().apply { uiState = it } },
        )
    }

    override var uiState by mutableStateOf(BookshelfEditDialogUiState())

    override fun showSelectionDialog() {
        uiState = uiState.copy(type = BookshelfEditDialogType.Selection)
    }

    override fun showRegisterDialog(bookshelfType: BookshelfType) {
        uiState = uiState.copy(
            type = BookshelfEditDialogType.Editor(
                BookshelfEditorType.Register(bookshelfType),
            ),
        )
    }

    override fun hideDialog() {
        uiState = uiState.copy(type = BookshelfEditDialogType.Hide, discard = false)
    }

    override fun showDiscard() {
        uiState = uiState.copy(discard = true)
    }

    override fun hideDiscard() {
        uiState = uiState.copy(discard = false)
    }

    override fun showEditorDialog(bookshelfId: BookshelfId, bookshelfType: BookshelfType) {
        uiState = uiState.copy(
            type = BookshelfEditDialogType.Editor(
                BookshelfEditorType.Edit(bookshelfId, bookshelfType),
            ),
        )
    }
}
