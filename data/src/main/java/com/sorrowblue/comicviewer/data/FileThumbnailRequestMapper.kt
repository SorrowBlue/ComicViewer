package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.common.ServerFileModel
import com.sorrowblue.comicviewer.domain.entity.FileThumbnailRequest

class FileThumbnailRequestMapper : Mapper<FileThumbnailRequest, ServerFileModel> {

    override fun map(data: FileThumbnailRequest, options: Options) =
        ServerFileModel(data.server.toServerModel() to data.file.toFileModel())
}
