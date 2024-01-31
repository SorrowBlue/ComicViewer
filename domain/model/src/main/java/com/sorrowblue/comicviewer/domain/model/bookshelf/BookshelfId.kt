package com.sorrowblue.comicviewer.domain.model.bookshelf

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@JvmInline
value class BookshelfId(val value: Int = 0) : Parcelable {

    companion object {
        val Default = BookshelfId(0)
    }
}
