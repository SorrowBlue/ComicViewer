package com.sorrowblue.comicviewer.data.common

data class SimpleFileModel(
    val path: String,
    val serverModelId: ServerModelId,
    val name: String,
    val parent: String,
    val size: Long,
    val lastModifier: Long,
    val type: FileModel,
    val sortIndex: Int
)

val String.extension get() = substringAfterLast('.').lowercase()

sealed interface FileModel {

    fun simple() = SimpleFileModel(path, serverModelId, name, parent, size, lastModifier, this, sortIndex)

    val extension get() = path.extension

    val path: String
    val serverModelId: ServerModelId
    val name: String
    val parent: String
    val size: Long
    val lastModifier: Long
    val sortIndex: Int

    data class File(
        override val path: String,
        override val serverModelId: ServerModelId,
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
        override val serverModelId: ServerModelId,
        override val name: String,
        override val parent: String,
        override val size: Long,
        override val lastModifier: Long,
        override val sortIndex: Int,
    ) : FileModel

    data class ImageFolder(
        override val path: String,
        override val serverModelId: ServerModelId,
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
