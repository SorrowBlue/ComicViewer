package com.sorrowblue.comicviewer.data.mapper

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteFileModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile

internal fun FavoriteFile.toFavoriteBookModel(): FavoriteFileModel {
    return FavoriteFileModel(FavoriteModelId(id.value), BookshelfModelId.from(bookshelfId), path)
}
