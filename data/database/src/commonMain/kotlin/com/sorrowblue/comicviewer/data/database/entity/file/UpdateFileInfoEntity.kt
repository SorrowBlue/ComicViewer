package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room.ColumnInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

internal class UpdateFileInfoEntity(
    val path: String,
    @ColumnInfo(name = FileEntity.BOOKSHELF_ID) val bookshelfId: BookshelfId,
    @ColumnInfo(name = "cache_key") val cacheKey: String = "",
    @ColumnInfo(name = "total_page_count") val totalPageCount: Int = 0,
)
