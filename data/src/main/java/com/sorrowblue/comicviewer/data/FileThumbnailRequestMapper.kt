package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FavoriteModelId
import com.sorrowblue.comicviewer.data.common.ServerFileModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.domain.request.FileThumbnailRequest

class FileThumbnailRequestMapper : Mapper<FileThumbnailRequest, ServerFileModel> {

    override fun map(data: FileThumbnailRequest, options: Options) =
        ServerFileModel(ServerModelId(data.serverId.value) to data.file.toFileModel())
}

class FavoriteMapper : Mapper<Favorite, FavoriteModel> {

    override fun map(data: Favorite, options: Options) =
        FavoriteModel(FavoriteModelId(data.id.value), data.name, data.count)
}
