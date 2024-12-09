package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.serialization.Serializable

sealed interface File {
    val bookshelfId: BookshelfId
    val name: String
    val parent: String
    val path: String
    val size: Long
    val lastModifier: Long
    val isHidden: Boolean

    val sortIndex: Int
    val cacheKey: String

    fun key() = Key(bookshelfId, path, parent)

    @Serializable
    data class Key(val bookshelfId: BookshelfId, val path: String, val parent: String)
}
