package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfFileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.request.FileThumbnailRequest

class FileThumbnailRequestMapper : Mapper<FileThumbnailRequest, BookshelfFileModel> {

    override fun map(data: FileThumbnailRequest, options: Options) =
        BookshelfFileModel(BookshelfModelId(data.serverId.value) to data.file.toFileModel())
}

class FavoriteMapper : Mapper<Favorite, FavoriteModel> {

    override fun map(data: Favorite, options: Options) =
        FavoriteModel(FavoriteModelId(data.id.value), data.name, data.count)
}
