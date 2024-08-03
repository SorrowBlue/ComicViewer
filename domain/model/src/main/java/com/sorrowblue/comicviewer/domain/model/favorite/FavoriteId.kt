package com.sorrowblue.comicviewer.domain.model.favorite

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@JvmInline
value class FavoriteId private constructor(val value: Int) : Parcelable {

    constructor() : this(0)

    interface Converter {
        fun toId(value: Int?): FavoriteId? = value?.let(::FavoriteId)

        fun toValue(value: FavoriteId?): Int? = value?.value
    }
}
