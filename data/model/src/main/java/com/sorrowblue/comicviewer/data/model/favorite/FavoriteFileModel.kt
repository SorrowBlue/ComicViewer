package com.sorrowblue.comicviewer.data.model.favorite

import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId

data class FavoriteFileModel(
    val id: FavoriteModelId,
    val bookshelfModelId: BookshelfModelId,
    val filePath: String
)
