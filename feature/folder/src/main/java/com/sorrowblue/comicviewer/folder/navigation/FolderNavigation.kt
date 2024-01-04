package com.sorrowblue.comicviewer.folder.navigation

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

class FolderArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val restorePath: String?,
)
