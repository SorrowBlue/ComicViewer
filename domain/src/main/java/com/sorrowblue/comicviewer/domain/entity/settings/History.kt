package com.sorrowblue.comicviewer.domain.entity.settings

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import kotlinx.serialization.Serializable

@Serializable
data class History(val bookshelfId: BookshelfId? = null, val path: String? = null, val position: Int? = null)


