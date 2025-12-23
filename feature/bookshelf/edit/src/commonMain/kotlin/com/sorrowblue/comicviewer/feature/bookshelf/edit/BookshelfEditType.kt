package com.sorrowblue.comicviewer.feature.bookshelf.edit

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import kotlinx.serialization.Serializable

@Serializable
sealed interface BookshelfEditType {
    val bookshelfType: BookshelfType

    @Serializable
    data class Register(override val bookshelfType: BookshelfType) : BookshelfEditType

    @Serializable
    data class Edit(val bookshelfId: BookshelfId, override val bookshelfType: BookshelfType) :
        BookshelfEditType
}
