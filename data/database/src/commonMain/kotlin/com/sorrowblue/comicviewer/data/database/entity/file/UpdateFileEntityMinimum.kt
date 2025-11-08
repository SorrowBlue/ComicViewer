package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder

internal class UpdateFileEntityMinimum(
    val path: String,
    @ColumnInfo(name = FileEntity.BookshelfId) val bookshelfId: BookshelfId,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "hidden") val isHidden: Boolean,
    @ColumnInfo(name = "file_type") val fileEntityType: FileEntity.Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileEntityType.order,
) {
    companion object {
        fun fromModel(model: File) = UpdateFileEntityMinimum(
            path = model.path,
            bookshelfId = model.bookshelfId,
            size = model.size,
            lastModified = model.lastModifier,
            isHidden = model.isHidden,
            fileEntityType = when (model) {
                is BookFile -> FileEntity.Type.FILE
                is BookFolder -> FileEntity.Type.IMAGE_FOLDER
                is Folder -> FileEntity.Type.FOLDER
            },
        )
    }
}
