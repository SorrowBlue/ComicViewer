package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity

internal data class BookshelfIdCacheKey(
    @ColumnInfo(name = FileEntity.BOOKSHELF_ID) val bookshelfId: Int,
    @ColumnInfo(name = "cache_key") val cacheKey: String = "",
)
