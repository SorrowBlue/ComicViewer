package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.domain.entity.file.File

class FileMapper : Mapper<File, FileModel> {

    override fun map(data: File, options: Options) = data.toFileModel()
}
