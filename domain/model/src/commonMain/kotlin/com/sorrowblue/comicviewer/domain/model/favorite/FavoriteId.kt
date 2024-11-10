package com.sorrowblue.comicviewer.domain.model.favorite

import com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class FavoriteId @ExperimentalIdValue constructor(val value: Int) {

    companion object {
        @OptIn(ExperimentalIdValue::class)
        operator fun invoke() = FavoriteId(0)
    }
}
