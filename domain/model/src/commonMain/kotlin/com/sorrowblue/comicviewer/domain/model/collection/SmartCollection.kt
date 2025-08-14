package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
data class SmartCollection private constructor(
    override val id: CollectionId,
    override val name: String,
    override val count: Int,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
    val bookshelfId: BookshelfId?,
    val searchCondition: SearchCondition,
) : Collection {

    fun copy(
        name: String = this.name,
        bookshelfId: BookshelfId? = this.bookshelfId,
        searchCondition: SearchCondition = this.searchCondition,
    ): SmartCollection {
        return copy(
            id = id,
            name = name,
            bookshelfId = bookshelfId,
            searchCondition = searchCondition,
        )
    }

    companion object {

        operator fun invoke(
            name: String,
            bookshelfId: BookshelfId?,
            searchCondition: SearchCondition,
        ) = SmartCollection(
            id = CollectionId(),
            name = name,
            count = 0,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            bookshelfId = bookshelfId,
            searchCondition = searchCondition
        )

        @InternalDataApi
        operator fun invoke(
            id: CollectionId,
            name: String,
            count: Int,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
            bookshelfId: BookshelfId?,
            searchCondition: SearchCondition,
        ) = SmartCollection(
            id = id,
            name = name,
            count = count,
            createdAt = createdAt,
            updatedAt = updatedAt,
            bookshelfId = bookshelfId,
            searchCondition = searchCondition
        )
    }
}
