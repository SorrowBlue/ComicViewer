package com.sorrowblue.comicviewer.domain.model.bookshelf

sealed interface Bookshelf {
    val id: BookshelfId
    val displayName: String
    val fileCount: Int
    val isDeleted: Boolean
    val type: BookshelfType?
}
