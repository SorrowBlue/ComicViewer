package com.sorrowblue.comicviewer.domain.model.favorite

import android.os.Parcelable
import com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@JvmInline
value class FavoriteId @ExperimentalIdValue constructor(val value: Int) : Parcelable {

    companion object {
        @OptIn(ExperimentalIdValue::class)
        operator fun invoke() = FavoriteId(0)
    }
}
