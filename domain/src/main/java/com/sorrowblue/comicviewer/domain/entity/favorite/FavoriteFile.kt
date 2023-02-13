package com.sorrowblue.comicviewer.domain.entity.favorite

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId

data class FavoriteFile(val id: FavoriteId, val bookshelfId: BookshelfId, val path: String)
