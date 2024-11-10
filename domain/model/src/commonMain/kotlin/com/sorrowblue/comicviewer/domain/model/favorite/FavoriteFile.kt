package com.sorrowblue.comicviewer.domain.model.favorite

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

data class FavoriteFile(val id: FavoriteId, val bookshelfId: BookshelfId, val path: String)
