package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile

@Entity(
    tableName = "read_later_file",
    primaryKeys = [ReadLaterFileEntity.BOOKSHELF_ID, ReadLaterFileEntity.FILE_PATH],
    foreignKeys = [
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = [FileEntity.PATH, FileEntity.BOOKSHELF_ID],
            childColumns = [ReadLaterFileEntity.FILE_PATH, ReadLaterFileEntity.BOOKSHELF_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [ReadLaterFileEntity.FILE_PATH, ReadLaterFileEntity.BOOKSHELF_ID])]
)
internal data class ReadLaterFileEntity(
    @ColumnInfo(FILE_PATH) val filePath: String,
    @ColumnInfo(BOOKSHELF_ID) val bookshelfId: Int,
) {
    companion object {
        const val FILE_PATH = "file_path"
        const val BOOKSHELF_ID = "bookshelf_id"
        fun fromModel(model: ReadLaterFile) =
            ReadLaterFileEntity(filePath = model.path, bookshelfId = model.bookshelfId.value)
    }
}
