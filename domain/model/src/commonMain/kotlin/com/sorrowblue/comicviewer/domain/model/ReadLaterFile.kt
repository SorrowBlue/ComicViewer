package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class ReadLaterFile(
    val bookshelfId: BookshelfId,
    val path: String,
    val modifiedDate: Long = Clock.System.now().toEpochMilliseconds(),
)
