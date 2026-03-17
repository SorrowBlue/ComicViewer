package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity

internal class EmbeddedBookshelfFileCountEntity(
    @Embedded val entity: BookshelfEntity,
    @Embedded val fileEntity: FileEntity,
    @ColumnInfo("file_count") val fileCount: Int,
)
