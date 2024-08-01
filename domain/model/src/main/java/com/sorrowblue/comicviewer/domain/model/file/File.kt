package com.sorrowblue.comicviewer.domain.model.file

import android.os.Parcelable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

sealed interface File : Parcelable {
    val bookshelfId: BookshelfId
    val name: String
    val parent: String
    val path: String
    val size: Long
    val lastModifier: Long
    val isHidden: Boolean

    val sortIndex: Int
    val cacheKey: String
}
