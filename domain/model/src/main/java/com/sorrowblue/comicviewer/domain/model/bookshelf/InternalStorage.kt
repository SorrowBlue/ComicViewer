package com.sorrowblue.comicviewer.domain.model.bookshelf

import kotlinx.parcelize.IgnoredOnParcel
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

@Parcelize
data object ShareContents : Bookshelf {
    @IgnoredOnParcel
    override val id: BookshelfId = BookshelfId(-1)

    @IgnoredOnParcel
    override val displayName: String = "ShareContents"

    @IgnoredOnParcel
    override val fileCount = 0
}
