package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.Embedded

internal class BookshelfFile(
    @Embedded val bookshelf: Bookshelf,
    @Embedded val file: File
)
