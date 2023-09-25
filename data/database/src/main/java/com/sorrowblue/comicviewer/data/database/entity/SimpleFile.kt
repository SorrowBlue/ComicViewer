package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.data.model.SimpleFileModel

internal class SimpleFile(
    val path: String,
    @ColumnInfo(name = File.BOOKSHELF_ID) val serverId: Int,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "file_type") val fileType: File.Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
) {
    companion object {
        fun fromModel(model: SimpleFileModel) = SimpleFile(
            path = model.path,
            serverId = model.bookshelfModelId.value,
            name = model.name,
            parent = model.parent,
            size = model.size,
            lastModified = model.lastModifier,
            fileType = when (model.type) {
                is FileModel.File -> File.Type.FILE
                is FileModel.Folder -> File.Type.FOLDER
                is FileModel.ImageFolder -> File.Type.IMAGE_FOLDER
            },
            sortIndex = model.sortIndex
        )
    }
}
