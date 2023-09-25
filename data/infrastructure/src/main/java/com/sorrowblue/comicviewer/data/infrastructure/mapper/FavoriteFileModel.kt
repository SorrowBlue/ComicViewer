package com.sorrowblue.comicviewer.data.infrastructure.mapper

import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.model.favorite.FavoriteFileModel
import com.sorrowblue.comicviewer.data.model.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile

internal fun FavoriteFile.toFavoriteBookModel(): FavoriteFileModel {
    return FavoriteFileModel(FavoriteModelId(id.value), BookshelfModelId.from(bookshelfId), path)
}
