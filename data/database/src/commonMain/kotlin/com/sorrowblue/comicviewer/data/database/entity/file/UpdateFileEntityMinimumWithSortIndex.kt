/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room3.ColumnInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

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

internal data class UpdateFileEntityMinimumWithSortIndexWithoutFileType(
    val path: String,
    @ColumnInfo(name = FileEntity.BookshelfId) val bookshelfId: BookshelfId,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "hidden") val isHidden: Boolean,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
) {
    companion object {
        fun fromFileEntity(entity: FileEntity) =
            UpdateFileEntityMinimumWithSortIndexWithoutFileType(
                path = entity.path,
                bookshelfId = BookshelfId(entity.bookshelfId),
                name = entity.name,
                parent = entity.parent,
                size = entity.size,
                lastModified = entity.lastModified,
                isHidden = entity.isHidden,
                sortIndex = entity.sortIndex,
            )
    }
}
