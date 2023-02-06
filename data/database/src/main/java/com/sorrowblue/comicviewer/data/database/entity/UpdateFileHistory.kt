package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo

data class UpdateFileHistory(
    val path: String,
    @ColumnInfo(name = File.BOOKSHELF_ID) val bookshelfId: Int,
    @ColumnInfo(name = "last_read_page") val lastReadPage: Int,
    @ColumnInfo(name = "last_read") val lastRead: Long
)
