package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

data class UpdateFileHistoryEntity(
    val path: String,
    @ColumnInfo(name = FileEntity.BOOKSHELF_ID) val bookshelfId: BookshelfId,
    @ColumnInfo(name = "last_read_page") val lastReadPage: Int,
    @ColumnInfo(name = "last_read") val lastReading: Long,
)
