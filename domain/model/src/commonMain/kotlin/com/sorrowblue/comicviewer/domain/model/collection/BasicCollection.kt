package com.sorrowblue.comicviewer.domain.model.collection

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
data class BasicCollection(
    override val id: CollectionId,
    override val name: String,
    override val count: Int,
    override val createdAt: LocalDateTime,
    override val updatedAt: LocalDateTime,
) : Collection {

    constructor(name: String) : this(
        id = CollectionId(),
        name = name,
        count = 0,
        createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    )
}
