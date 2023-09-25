package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo

data class FileHistoryEntity(
    @ColumnInfo(name = "last_read_page") val lastReadPage: Int = 0,
    @ColumnInfo(name = "last_read") val lastReading: Long = 0,
)
