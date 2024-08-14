package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room.ColumnInfo

internal data class EmbeddedFileInfoEntity(
    @ColumnInfo(name = "cache_key") val cacheKey: String = "",
    @ColumnInfo(name = "total_page_count") val totalPageCount: Int = 0,
)
