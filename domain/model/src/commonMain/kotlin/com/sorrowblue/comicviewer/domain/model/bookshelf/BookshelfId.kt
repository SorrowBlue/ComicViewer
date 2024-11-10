package com.sorrowblue.comicviewer.domain.model.bookshelf

import com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class BookshelfId @ExperimentalIdValue constructor(val value: Int) {
    companion object {
        @OptIn(ExperimentalIdValue::class)
        operator fun invoke() = BookshelfId(0)
    }
}
