package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.datetime.Clock

data class ReadLaterFile(
    val bookshelfId: BookshelfId,
    val path: String,
    val modifiedDate: Long = Clock.System.now().toEpochMilliseconds(),
)
