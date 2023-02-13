package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

internal class BookshelfFileCount(
    @Embedded val bookshelf: Bookshelf,
    @Embedded val file: File,
    @ColumnInfo("file_count") val fileCount: Int
)
