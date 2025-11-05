package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder

@Entity(
    tableName = "file",
    primaryKeys = [FileEntity.PATH, FileEntity.BOOKSHELF_ID],
    foreignKeys = [
        ForeignKey(
            entity = BookshelfEntity::class,
            parentColumns = [BookshelfEntity.ID],
            childColumns = [FileEntity.BOOKSHELF_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = [FileEntity.BOOKSHELF_ID, FileEntity.PATH])],
)
internal data class FileEntity(
    @ColumnInfo(PATH) val path: String,
    @ColumnInfo(BOOKSHELF_ID) val bookshelfId: Int,
    val name: String,
    val parent: String,
    val size: Long,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "hidden", defaultValue = "false") val isHidden: Boolean,
    @ColumnInfo(name = "file_type") val fileType: Type,
    @ColumnInfo(name = "file_type_order") val fileTypeOrder: Int = fileType.order,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
    @Embedded val info: EmbeddedFileInfoEntity = EmbeddedFileInfoEntity(),
    @Embedded val history: EmbeddedFileHistoryEntity = EmbeddedFileHistoryEntity(),
) {
    companion object {
        const val PATH = "path"
        const val BOOKSHELF_ID = "bookshelf_id"

        fun fromModel(model: File) = when (model) {
            is BookFile -> FileEntity(
                path = model.path,
                bookshelfId = model.bookshelfId.value,
                name = model.name,
                parent = model.parent,
                size = model.size,
                lastModified = model.lastModifier,
                isHidden = model.isHidden,
                fileType = Type.FILE,
                sortIndex = model.sortIndex,
                info = EmbeddedFileInfoEntity(model.cacheKey, model.totalPageCount),
                history = EmbeddedFileHistoryEntity(model.lastPageRead, model.lastReadTime),
            )

            is Folder -> FileEntity(
                path = model.path,
                bookshelfId = model.bookshelfId.value,
                name = model.name,
                parent = model.parent,
                size = model.size,
                lastModified = model.lastModifier,
                isHidden = model.isHidden,
                fileType = Type.FOLDER,
                sortIndex = model.sortIndex,
                info = EmbeddedFileInfoEntity("", 0),
                history = EmbeddedFileHistoryEntity(0, 0),
            )

            is BookFolder -> FileEntity(
                path = model.path,
                bookshelfId = model.bookshelfId.value,
                name = model.name,
                parent = model.parent,
                size = model.size,
                lastModified = model.lastModifier,
                isHidden = model.isHidden,
                fileType = Type.IMAGE_FOLDER,
                sortIndex = model.sortIndex,
                info = EmbeddedFileInfoEntity(model.cacheKey, model.totalPageCount),
                history = EmbeddedFileHistoryEntity(model.lastPageRead, model.lastReadTime),
            )
        }
    }

    fun toModel(): File = when (fileType) {
        Type.FILE -> BookFile(
            path = path,
            bookshelfId = BookshelfId(bookshelfId),
            parent = parent,
            name = name,
            size = size,
            lastModifier = lastModified,
            isHidden = isHidden,
            sortIndex = sortIndex,
            cacheKey = info.cacheKey,
            totalPageCount = info.totalPageCount,
            lastPageRead = history.lastReadPage,
            lastReadTime = history.lastReading,
        )

        Type.FOLDER -> Folder(
            path = path,
            bookshelfId = BookshelfId(bookshelfId),
            name = name,
            parent = parent,
            size = size,
            lastModifier = lastModified,
            isHidden = isHidden,
            sortIndex = sortIndex,
        )

        Type.IMAGE_FOLDER -> BookFolder(
            path = path,
            bookshelfId = BookshelfId(bookshelfId),
            name = name,
            parent = parent,
            size = size,
            lastModifier = lastModified,
            isHidden = isHidden,
            sortIndex = sortIndex,
            cacheKey = info.cacheKey,
            totalPageCount = info.totalPageCount,
            lastPageRead = history.lastReadPage,
            lastReadTime = history.lastReading,
        )
    }

    enum class Type(val order: Int) {
        FILE(1),
        FOLDER(0),
        IMAGE_FOLDER(0),
    }
}
