package com.sorrowblue.comicviewer.domain.model.bookshelf

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@JvmInline
value class BookshelfId(val value: Int = 0) : Parcelable {

    companion object {
        const val Default = 0
    }
}
