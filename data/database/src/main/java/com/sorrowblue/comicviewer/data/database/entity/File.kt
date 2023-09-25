package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId

data class FileWithCount(
    @ColumnInfo(File.PATH) val path: String,
    @ColumnInfo(File.BOOKSHELF_ID) val bookshelfId: Int,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "file_type") val fileType: File.Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
    @Embedded val info: FileInfo,
    @Embedded val history: FileHistory,
    val count: Int
) {

    fun toModel(): FileModel {
        return when (fileType) {
            File.Type.FILE -> FileModel.File(
                path = path,
                bookshelfModelId = BookshelfModelId(bookshelfId),
                parent = parent,
                name = name,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastReadPage = history.lastReadPage,
                lastReading = history.lastReading
            )

            File.Type.FOLDER -> FileModel.Folder(
                path = path,
                bookshelfModelId = BookshelfModelId(bookshelfId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                count = count
            )

            File.Type.IMAGE_FOLDER -> FileModel.ImageFolder(
                path = path,
                bookshelfModelId = BookshelfModelId(bookshelfId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastReadPage = history.lastReadPage,
                lastReading = history.lastReading,
                count = count
            )
        }
    }
}

@Entity(
    tableName = "file",
    primaryKeys = [File.PATH, File.BOOKSHELF_ID],
    foreignKeys = [ForeignKey(
        entity = Bookshelf::class,
        parentColumns = [Bookshelf.ID],
        childColumns = [File.BOOKSHELF_ID],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = [File.BOOKSHELF_ID, File.PATH])]
)
data class File(
    @ColumnInfo(PATH) val path: String,
    @ColumnInfo(BOOKSHELF_ID) val bookshelfId: Int,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "file_type") val fileType: Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
    @Embedded val info: FileInfo = FileInfo(),
    @Embedded val history: FileHistory = FileHistory()
) {

    companion object {
        const val PATH = "path"
        const val BOOKSHELF_ID = "bookshelf_id"

        fun fromModel(model: FileModel) =
            when (model) {
                is FileModel.File -> File(
                    path = model.path,
                    bookshelfId = model.bookshelfModelId.value,
                    name = model.name,
                    parent = model.parent,
                    size = model.size,
                    lastModified = model.lastModifier,
                    fileType = Type.FILE,
                    sortIndex = model.sortIndex,
                    info = FileInfo(model.cacheKey, model.totalPageCount),
                    history = FileHistory(model.lastReadPage, model.lastReading)
                )

                is FileModel.Folder -> File(
                    path = model.path,
                    bookshelfId = model.bookshelfModelId.value,
                    name = model.name,
                    parent = model.parent,
                    size = model.size,
                    lastModified = model.lastModifier,
                    fileType = Type.FOLDER,
                    sortIndex = model.sortIndex,
                    info = FileInfo("", 0),
                    history = FileHistory(0, 0)
                )

                is FileModel.ImageFolder -> File(
                    path = model.path,
                    bookshelfId = model.bookshelfModelId.value,
                    name = model.name,
                    parent = model.parent,
                    size = model.size,
                    lastModified = model.lastModifier,
                    fileType = Type.IMAGE_FOLDER,
                    sortIndex = model.sortIndex,
                    info = FileInfo(model.cacheKey, model.totalPageCount),
                    history = FileHistory(model.lastReadPage, model.lastReading)
                )
            }


    }

    fun toModel(): FileModel {
        return when (fileType) {
            Type.FILE -> FileModel.File(
                path = path,
                bookshelfModelId = BookshelfModelId(bookshelfId),
                parent = parent,
                name = name,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastReadPage = history.lastReadPage,
                lastReading = history.lastReading
            )

            Type.FOLDER -> FileModel.Folder(
                path = path,
                bookshelfModelId = BookshelfModelId(bookshelfId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex
            )

            Type.IMAGE_FOLDER -> FileModel.ImageFolder(
                path = path,
                bookshelfModelId = BookshelfModelId(bookshelfId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastReadPage = history.lastReadPage,
                lastReading = history.lastReading,
            )
        }
    }

    enum class Type(val order: Int) {
        FILE(1),
        FOLDER(0),
        IMAGE_FOLDER(0)
    }
}
