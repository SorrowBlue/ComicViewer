package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder

internal class SimpleFileEntity(
    val path: String,
    @ColumnInfo(name = FileEntity.BOOKSHELF_ID) val bookshelfId: BookshelfId,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "hidden") val isHidden: Boolean,
    @ColumnInfo(name = "file_type") val fileEntityType: FileEntity.Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileEntityType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
) {
    companion object {
        fun fromModel(model: File) = SimpleFileEntity(
            path = model.path,
            bookshelfId = model.bookshelfId,
            name = model.name,
            parent = model.parent,
            size = model.size,
            lastModified = model.lastModifier,
            isHidden = model.isHidden,
            fileEntityType = when (model) {
                is BookFile -> FileEntity.Type.FILE
                is BookFolder -> FileEntity.Type.IMAGE_FOLDER
                is Folder -> FileEntity.Type.FOLDER
            },
            sortIndex = model.sortIndex
        )
    }
}
