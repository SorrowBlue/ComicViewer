package com.sorrowblue.comicviewer.domain.entity.file

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

sealed interface File {
    val bookshelfId: BookshelfId
    val name: String
    val parent: String
    val path: String
    val size: Long
    val lastModifier: Long

    val params: Map<String, String?>

    @OptIn(ExperimentalEncodingApi::class)
    fun base64Parent(): String = Base64.encode(parent.encodeToByteArray())

    @OptIn(ExperimentalEncodingApi::class)
    fun base64Path(): String = Base64.encode(path.encodeToByteArray())

    fun areContentsTheSame(file: File): Boolean
}
