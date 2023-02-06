package com.sorrowblue.comicviewer.data.common

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId

sealed interface FileModel {

    val path: String
    val bookshelfModelId: BookshelfModelId
    val name: String
    val parent: String
    val size: Long
    val lastModifier: Long
    val sortIndex: Int

    val extension get() = path.extension

    fun simple() =
        SimpleFileModel(path, bookshelfModelId, name, parent, size, lastModifier, this, sortIndex)

    data class File(
        override val path: String,
        override val bookshelfModelId: BookshelfModelId,
        override val name: String,
        override val parent: String,
        override val size: Long,
        override val lastModifier: Long,
        override val sortIndex: Int,
        val cacheKey: String,
        val totalPageCount: Int,
        val lastReadPage: Int,
        val lastRead: Long
    ) : FileModel

    data class Folder(
        override val path: String,
        override val bookshelfModelId: BookshelfModelId,
        override val name: String,
        override val parent: String,
        override val size: Long,
        override val lastModifier: Long,
        override val sortIndex: Int,
    ) : FileModel

    data class ImageFolder(
        override val path: String,
        override val bookshelfModelId: BookshelfModelId,
        override val name: String,
        override val parent: String,
        override val size: Long,
        override val lastModifier: Long,
        override val sortIndex: Int,
        val cacheKey: String,
        val totalPageCount: Int,
        val lastReadPage: Int,
        val lastRead: Long
    ) : FileModel
}
