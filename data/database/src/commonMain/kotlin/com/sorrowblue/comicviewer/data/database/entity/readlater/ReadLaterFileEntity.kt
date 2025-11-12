package com.sorrowblue.comicviewer.data.database.entity.readlater

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

@Entity(
    tableName = "read_later_file",
    primaryKeys = [ReadLaterFileEntity.BookshelfId, ReadLaterFileEntity.FilePath],
    foreignKeys = [
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = [FileEntity.PATH, FileEntity.BookshelfId],
            childColumns = [ReadLaterFileEntity.FilePath, ReadLaterFileEntity.BookshelfId],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = [ReadLaterFileEntity.FilePath, ReadLaterFileEntity.BookshelfId])],
)
internal data class ReadLaterFileEntity(
    @ColumnInfo(FilePath) val filePath: String,
    @ColumnInfo(BookshelfId) val bookshelfId: BookshelfId,
    @ColumnInfo("modified_date", defaultValue = "-1") val modifiedDate: Long,
) {
    companion object {
        const val FilePath = "file_path"
        const val BookshelfId = "bookshelf_id"

        fun fromModel(model: ReadLaterFile) = ReadLaterFileEntity(
            filePath = model.path,
            bookshelfId = model.bookshelfId,
            modifiedDate = model.modifiedDate,
        )
    }
}
