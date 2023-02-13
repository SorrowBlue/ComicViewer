package com.sorrowblue.comicviewer.domain.entity.bookshelf

sealed interface Bookshelf {
    val id: BookshelfId
    val displayName: String
    val fileCount: Int
}
