package com.sorrowblue.comicviewer.domain.model.bookshelf

data class InternalStorage(
    override val id: BookshelfId,
    override val displayName: String,
    override val fileCount: Int,
) : Bookshelf {
    constructor(displayName: String) : this(BookshelfId(), displayName, 0)
    constructor(id: BookshelfId, displayName: String) : this(id, displayName, 0)
}

data object ShareContents : Bookshelf {
    override val id: BookshelfId = BookshelfId()

    override val displayName: String = "ShareContents"

    override val fileCount = 0
}
