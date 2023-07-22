package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.domain.entity.file.Folder

internal class FolderMapper : Mapper<Folder, FileModel.Folder> {

    override fun map(data: Folder, options: Options) = data.toFileModel() as FileModel.Folder
}
