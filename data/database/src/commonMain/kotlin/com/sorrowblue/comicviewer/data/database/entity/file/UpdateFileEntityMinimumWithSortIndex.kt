package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder

internal data class UpdateFileEntityMinimumWithSortIndex(
    val path: String,
    @ColumnInfo(name = FileEntity.BookshelfId) val bookshelfId: BookshelfId,
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
        fun fromModel(model: File) = UpdateFileEntityMinimumWithSortIndex(
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
            sortIndex = model.sortIndex,
        )

        fun fromFileEntity(entity: FileEntity) = UpdateFileEntityMinimumWithSortIndex(
            path = entity.path,
            bookshelfId = BookshelfId(entity.bookshelfId),
            name = entity.name,
            parent = entity.parent,
            size = entity.size,
            lastModified = entity.lastModified,
            isHidden = entity.isHidden,
            fileEntityType = entity.fileType,
            sortIndex = entity.sortIndex,
        )
    }
}
