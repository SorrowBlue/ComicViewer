package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo

data class UpdateFileHistoryEntity(
    val path: String,
    @ColumnInfo(name = FileEntity.BOOKSHELF_ID) val bookshelfId: Int,
    @ColumnInfo(name = "last_read_page") val lastReadPage: Int,
    @ColumnInfo(name = "last_read") val lastReading: Long,
)
