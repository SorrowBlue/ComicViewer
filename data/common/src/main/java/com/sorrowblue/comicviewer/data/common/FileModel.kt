package com.sorrowblue.comicviewer.data.common

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId

sealed interface FileModel {

    companion object

    val path: String
    val bookshelfModelId: BookshelfModelId
    val name: String
    val parent: String
    val size: Long
    val lastModifier: Long
    val sortIndex: Int

    val extension get() = path.substringAfterLast('.').lowercase()

    fun toSimpleModel() =
        SimpleFileModel(path, bookshelfModelId, name, parent, size, lastModifier, this, sortIndex)

    sealed interface Book : FileModel {
        val cacheKey: String
        val totalPageCount: Int
        val lastReadPage: Int
        val lastReading: Long
    }

    data class File(
        override val path: String,
        override val bookshelfModelId: BookshelfModelId,
        override val name: String,
        override val parent: String,
        override val size: Long,
        override val lastModifier: Long,
        override val sortIndex: Int,
        override val cacheKey: String,
        override val totalPageCount: Int,
        override val lastReadPage: Int,
        override val lastReading: Long
    ) : Book

    data class Folder(
        override val path: String,
        override val bookshelfModelId: BookshelfModelId,
        override val name: String,
        override val parent: String,
        override val size: Long,
        override val lastModifier: Long,
        override val sortIndex: Int,
        val count: Int = 0
    ) : FileModel

    data class ImageFolder(
        override val path: String,
        override val bookshelfModelId: BookshelfModelId,
        override val name: String,
        override val parent: String,
        override val size: Long,
        override val lastModifier: Long,
        override val sortIndex: Int,
        override val cacheKey: String,
        override val totalPageCount: Int,
        override val lastReadPage: Int,
        override val lastReading: Long,
        val count: Int = 0
    ) : Book
}
