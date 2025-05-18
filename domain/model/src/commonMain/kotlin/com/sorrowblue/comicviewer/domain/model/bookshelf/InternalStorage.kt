package com.sorrowblue.comicviewer.domain.model.bookshelf

data class InternalStorage(
    override val id: BookshelfId,
    override val displayName: String,
    override val fileCount: Int,
    override val isDeleted: Boolean = false,
) : Bookshelf {
    constructor(displayName: String) : this(BookshelfId(), displayName, 0)
    constructor(id: BookshelfId, displayName: String) : this(id, displayName, 0)
}
