package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class SmartCollection(
    override val id: CollectionId,
    override val name: String,
    override val count: Int,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
    val bookshelfId: BookshelfId?,
    val searchCondition: SearchCondition,
) : Collection {

    @OptIn(ExperimentalIdValue::class)
    constructor(name: String, bookshelfId: BookshelfId?, searchCondition: SearchCondition) : this(
        id = CollectionId(0),
        name = name,
        count = 0,
        createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        bookshelfId = bookshelfId,
        searchCondition = searchCondition
    )
}
