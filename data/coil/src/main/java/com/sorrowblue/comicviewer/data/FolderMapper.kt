package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.infrastructure.mapper.from
import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.domain.model.file.Folder

internal class FolderMapper : Mapper<Folder, FileModel.Folder> {

    override fun map(data: Folder, options: Options) = FileModel.from(data) as FileModel.Folder
}
