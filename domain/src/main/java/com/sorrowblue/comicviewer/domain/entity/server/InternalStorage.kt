package com.sorrowblue.comicviewer.domain.entity.server

data class InternalStorage(
    override val id: BookshelfId,
    override val displayName: String
) : Bookshelf {
    constructor(displayName: String) : this(BookshelfId(0), displayName)
}
