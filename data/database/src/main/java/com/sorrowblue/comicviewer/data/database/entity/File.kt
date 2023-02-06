package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId


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
                    history = FileHistory(model.lastReadPage, model.lastRead)
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
                    history = FileHistory(model.lastReadPage, model.lastRead)
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
                lastRead = history.lastRead
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
                lastRead = history.lastRead,
            )
        }
    }

    enum class Type(val order: Int) {
        FILE(1),
        FOLDER(0),
        IMAGE_FOLDER(0)
    }
}
