package com.sorrowblue.comicviewer.domain.model.file

import android.os.Parcelable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.parcelize.Parcelize

sealed interface File : Parcelable {
    val bookshelfId: BookshelfId
    val name: String
    val parent: String
    val path: String
    val size: Long
    val lastModifier: Long

    val params: Map<String, String?>
    val sortIndex: Int

    @OptIn(ExperimentalEncodingApi::class)
    fun base64Parent(): String = Base64.UrlSafe.encode(parent.encodeToByteArray())

    @OptIn(ExperimentalEncodingApi::class)
    fun base64Path(): String = Base64.UrlSafe.encode(path.encodeToByteArray())

    fun areContentsTheSame(file: File): Boolean
}

@Parcelize
data class FileAttribute(
    val archive: Boolean,
    val compressed: Boolean,
    val directory: Boolean,
    val normal: Boolean,
    val readonly: Boolean,
    val system: Boolean,
    val temporary: Boolean,
    val sharedRead: Boolean,
    val hidden: Boolean,
    val volume: Boolean,
) : Parcelable
