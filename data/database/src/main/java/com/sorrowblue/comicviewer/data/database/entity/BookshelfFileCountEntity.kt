package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

internal class BookshelfFileCountEntity(
    @Embedded val entity: BookshelfEntity,
    @Embedded val fileEntity: FileEntity,
    @ColumnInfo("file_count") val fileCount: Int,
)
