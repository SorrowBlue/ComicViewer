package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder

data class FileWithCountEntity(
    @ColumnInfo(FileEntity.PATH) val path: String,
    @ColumnInfo(FileEntity.BOOKSHELF_ID) val bookshelfId: Int,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "file_type") val fileEntityType: FileEntity.Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileEntityType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
    @Embedded val info: FileInfoEntity,
    @Embedded val history: FileHistoryEntity,
    val count: Int,
) {

    fun toModel(): File {
        return when (fileEntityType) {
            FileEntity.Type.FILE -> BookFile(
                path = path,
                bookshelfId = BookshelfId(bookshelfId),
                parent = parent,
                name = name,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastPageRead = history.lastReadPage,
                lastReadTime = history.lastReading
            )

            FileEntity.Type.FOLDER -> Folder(
                path = path,
                bookshelfId = BookshelfId(bookshelfId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                count = count
            )

            FileEntity.Type.IMAGE_FOLDER -> BookFolder(
                path = path,
                bookshelfId = BookshelfId(bookshelfId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastPageRead = history.lastReadPage,
                lastReadTime = history.lastReading,
                count = count
            )
        }
    }
}

@Entity(
    tableName = "file",
    primaryKeys = [FileEntity.PATH, FileEntity.BOOKSHELF_ID],
    foreignKeys = [
        ForeignKey(
            entity = BookshelfEntity::class,
            parentColumns = [BookshelfEntity.ID],
            childColumns = [FileEntity.BOOKSHELF_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [FileEntity.BOOKSHELF_ID, FileEntity.PATH])]
)
data class FileEntity(
    @ColumnInfo(PATH) val path: String,
    @ColumnInfo(BOOKSHELF_ID) val bookshelfId: Int,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "file_type") val fileType: Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
    @Embedded val info: FileInfoEntity = FileInfoEntity(),
    @Embedded val history: FileHistoryEntity = FileHistoryEntity(),
) {

    companion object {
        const val PATH = "path"
        const val BOOKSHELF_ID = "bookshelf_id"

        fun fromModel(model: File) =
            when (model) {
                is BookFile -> FileEntity(
                    path = model.path,
                    bookshelfId = model.bookshelfId.value,
                    name = model.name,
                    parent = model.parent,
                    size = model.size,
                    lastModified = model.lastModifier,
                    fileType = Type.FILE,
                    sortIndex = model.sortIndex,
                    info = FileInfoEntity(model.cacheKey, model.totalPageCount),
                    history = FileHistoryEntity(model.lastPageRead, model.lastReadTime)
                )

                is Folder -> FileEntity(
                    path = model.path,
                    bookshelfId = model.bookshelfId.value,
                    name = model.name,
                    parent = model.parent,
                    size = model.size,
                    lastModified = model.lastModifier,
                    fileType = Type.FOLDER,
                    sortIndex = model.sortIndex,
                    info = FileInfoEntity("", 0),
                    history = FileHistoryEntity(0, 0)
                )

                is BookFolder -> FileEntity(
                    path = model.path,
                    bookshelfId = model.bookshelfId.value,
                    name = model.name,
                    parent = model.parent,
                    size = model.size,
                    lastModified = model.lastModifier,
                    fileType = Type.IMAGE_FOLDER,
                    sortIndex = model.sortIndex,
                    info = FileInfoEntity(model.cacheKey, model.totalPageCount),
                    history = FileHistoryEntity(model.lastPageRead, model.lastReadTime)
                )
            }
    }

    fun toModel(): File {
        return when (fileType) {
            Type.FILE -> BookFile(
                path = path,
                bookshelfId = BookshelfId(bookshelfId),
                parent = parent,
                name = name,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastPageRead = history.lastReadPage,
                lastReadTime = history.lastReading
            )

            Type.FOLDER -> Folder(
                path = path,
                bookshelfId = BookshelfId(bookshelfId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex
            )

            Type.IMAGE_FOLDER -> BookFolder(
                path = path,
                bookshelfId = BookshelfId(bookshelfId),
                name = name,
                parent = parent,
                size = size,
                lastModifier = lastModified,
                sortIndex = sortIndex,
                cacheKey = info.cacheKey,
                totalPageCount = info.totalPageCount,
                lastPageRead = history.lastReadPage,
                lastReadTime = history.lastReading
            )
        }
    }

    enum class Type(val order: Int) {
        FILE(1),
        FOLDER(0),
        IMAGE_FOLDER(0),
    }
}
