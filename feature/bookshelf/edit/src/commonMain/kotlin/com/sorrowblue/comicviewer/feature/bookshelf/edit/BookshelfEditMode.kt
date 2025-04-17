package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_title_edit
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_title_register
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
sealed class BookshelfEditMode {

    internal abstract val title: String
        @Composable get

    @Serializable
    data class Register(val bookshelfType: BookshelfType) : BookshelfEditMode() {
        override val title @Composable get() = stringResource(Res.string.bookshelf_edit_title_register)
    }

    @Serializable
    data class Edit(val bookshelfId: BookshelfId) : BookshelfEditMode() {
        override val title @Composable get() = stringResource(Res.string.bookshelf_edit_title_edit)
    }
}
