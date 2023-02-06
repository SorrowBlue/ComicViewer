package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo

data class FileHistory(
    @ColumnInfo(name = "last_read_page") val lastReadPage: Int = 0,
    @ColumnInfo(name = "last_read") val lastRead: Long = 0,
)
