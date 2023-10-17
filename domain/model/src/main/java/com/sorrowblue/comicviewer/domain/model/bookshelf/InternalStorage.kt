package com.sorrowblue.comicviewer.domain.model.bookshelf

import kotlinx.parcelize.Parcelize

@Parcelize
data class InternalStorage(
    override val id: BookshelfId,
    override val displayName: String,
    override val fileCount: Int,
) : Bookshelf {
    constructor(displayName: String) : this(BookshelfId(0), displayName, 0)
    constructor(id: BookshelfId, displayName: String) : this(id, displayName, 0)
}
