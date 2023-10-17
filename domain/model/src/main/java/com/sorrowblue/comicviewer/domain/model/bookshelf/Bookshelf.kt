package com.sorrowblue.comicviewer.domain.model.bookshelf

import android.os.Parcelable

sealed interface Bookshelf : Parcelable {
    val id: BookshelfId
    val displayName: String
    val fileCount: Int
}
