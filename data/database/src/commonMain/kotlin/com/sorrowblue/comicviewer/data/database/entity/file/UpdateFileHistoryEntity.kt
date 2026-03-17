package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room3.ColumnInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

internal class UpdateFileHistoryEntity(
    val path: String,
    @ColumnInfo(name = FileEntity.BookshelfId) val bookshelfId: BookshelfId,
    @ColumnInfo(name = "last_read_page") val lastReadPage: Int,
    @ColumnInfo(name = "last_read") val lastReading: Long,
)
