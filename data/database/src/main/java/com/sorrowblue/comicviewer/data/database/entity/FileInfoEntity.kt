package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo

data class FileInfoEntity(
    @ColumnInfo(name = "cache_key") val cacheKey: String = "",
    @ColumnInfo(name = "total_page_count") val totalPageCount: Int = 0,
)
