package com.sorrowblue.comicviewer.domain.entity.server

sealed interface Bookshelf {
    val id: BookshelfId
    val displayName: String
}
