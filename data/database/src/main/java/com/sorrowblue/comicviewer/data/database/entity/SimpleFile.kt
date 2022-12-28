package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.SimpleFileModel

internal class SimpleFile(
    val path: String,
    @ColumnInfo(name = SERVER_ID) val serverId: Int,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "file_type") val fileType: File.Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
)

internal fun SimpleFileModel.toSimpleFile(): SimpleFile {
    val type = when (type) {
        is FileModel.File -> File.Type.FILE
        is FileModel.Folder -> File.Type.FOLDER
        is FileModel.ImageFolder -> File.Type.IMAGE_FOLDER
    }
    return SimpleFile(
        path = path,
        serverId = serverModelId.value,
        name = name,
        parent = parent,
        size = size,
        lastModified = lastModifier,
        fileType = type,
        sortIndex = sortIndex
    )
}
