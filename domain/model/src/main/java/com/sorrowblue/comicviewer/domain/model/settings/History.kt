package com.sorrowblue.comicviewer.domain.model.settings

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.serialization.Serializable

@Serializable
data class History(
    val bookshelfId: BookshelfId? = null,
    val path: String? = null,
    val position: Int? = null
)


