package com.sorrowblue.comicviewer.feature.bookshelf.edit

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface BookshelfEditorType {

    val bookshelfType: BookshelfType

    @Serializable
    data class Register(override val bookshelfType: BookshelfType) : BookshelfEditorType

    @Serializable
    data class Edit(val bookshelfId: BookshelfId, override val bookshelfType: BookshelfType) :
        BookshelfEditorType
}
