package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity

internal class EmbeddedBookshelfFileCountEntity(
    @Embedded val entity: BookshelfEntity,
    @Embedded val fileEntity: FileEntity,
    @ColumnInfo("file_count") val fileCount: Int,
)
