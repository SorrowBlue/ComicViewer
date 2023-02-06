package com.sorrowblue.comicviewer.data.common.favorite

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId

data class FavoriteFileModel(
    val id: FavoriteModelId,
    val bookshelfModelId: BookshelfModelId,
    val filePath: String
)
