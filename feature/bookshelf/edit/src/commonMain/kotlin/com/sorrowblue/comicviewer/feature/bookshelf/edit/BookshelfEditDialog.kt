package com.sorrowblue.comicviewer.feature.bookshelf.edit

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
