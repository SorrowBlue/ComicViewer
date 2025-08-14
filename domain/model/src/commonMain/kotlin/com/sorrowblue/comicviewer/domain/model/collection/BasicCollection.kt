package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
data class BasicCollection private constructor(
    override val id: CollectionId,
    override val name: String,
    override val count: Int,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : Collection {

    fun copy(name: String = this.name) = copy(
        id = id,
        name = name,
    )

    companion object {

        operator fun invoke(name: String) = BasicCollection(
            id = CollectionId(),
            name = name,
            count = 0,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        @InternalDataApi
        operator fun invoke(
            id: CollectionId,
            name: String,
            count: Int,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
        ) = BasicCollection(
            id = id,
            name = name,
            count = count,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}
