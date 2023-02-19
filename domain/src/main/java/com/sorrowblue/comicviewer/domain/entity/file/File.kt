package com.sorrowblue.comicviewer.domain.entity.file

import android.util.Base64
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId

sealed interface File {
    val bookshelfId: BookshelfId
    val name: String
    val parent: String
    val path: String
    val size: Long
    val lastModifier: Long

    val params: Map<String, String?>

    fun base64Path(): String =
        Base64.encodeToString(path.encodeToByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
}
