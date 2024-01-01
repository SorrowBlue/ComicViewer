package com.sorrowblue.comicviewer.domain.model.favorite

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@JvmInline
value class FavoriteId(val value: Int) : Parcelable {

    companion object {
        val Default = FavoriteId(-1)
    }
}
