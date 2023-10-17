package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder

internal class SimpleFileEntity(
    val path: String,
    @ColumnInfo(name = FileEntity.BOOKSHELF_ID) val serverId: Int,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "file_type") val fileEntityType: FileEntity.Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileEntityType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
) {
    companion object {
        fun fromModel(model: File) = SimpleFileEntity(
            path = model.path,
            serverId = model.bookshelfId.value,
            name = model.name,
            parent = model.parent,
            size = model.size,
            lastModified = model.lastModifier,
            fileEntityType = when (model) {
                is BookFile -> FileEntity.Type.FILE
                is BookFolder -> FileEntity.Type.IMAGE_FOLDER
                is Folder -> FileEntity.Type.FOLDER
            },
            sortIndex = model.sortIndex
        )
    }
}
