package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue
import kotlin.jvm.JvmInline
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

sealed interface Collection {

    val id: CollectionId
    val name: String
    val count: Int
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
}

data class CollectionCriteria(
    val type: CollectionType = CollectionType.All,
    val recent: Boolean = false,
)

enum class CollectionType {
    Smart,
    Basic,
    All,
}

@Serializable
@JvmInline
value class CollectionId @ExperimentalIdValue constructor(val value: Int) {

    companion object {
        @OptIn(ExperimentalIdValue::class)
        operator fun invoke() = CollectionId(0)
    }
}
