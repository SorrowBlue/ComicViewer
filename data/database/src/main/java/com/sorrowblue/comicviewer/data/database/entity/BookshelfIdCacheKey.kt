package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo

data class BookshelfIdCacheKey(
    @ColumnInfo(name = FileEntity.BOOKSHELF_ID) val bookshelfId: Int,
    @ColumnInfo(name = "cache_key") val cacheKey: String = "",
)
