package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room.ColumnInfo

internal data class EmbeddedFileHistoryEntity(
    @ColumnInfo(name = "last_read_page") val lastReadPage: Int = 0,
    @ColumnInfo(name = "last_read") val lastReading: Long = 0,
)
