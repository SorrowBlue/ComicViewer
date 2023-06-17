package com.sorrowblue.comicviewer.domain.entity.file

import com.sorrowblue.comicviewer.domain.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId

sealed interface File {
    val bookshelfId: BookshelfId
    val name: String
    val parent: String
    val path: String
    val size: Long
    val lastModifier: Long

    val params: Map<String, String?>

    fun base64Parent(): String = parent.encodeToBase64()
    fun base64Path(): String = path.encodeToBase64()

    fun areContentsTheSame(file: File): Boolean
}
