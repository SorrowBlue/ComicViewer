package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.common.ReadLaterFileModel

@Entity(
    tableName = "read_later_file",
    primaryKeys = [ReadLaterFile.BOOKSHELF_ID, ReadLaterFile.FILE_PATH],
    foreignKeys = [
        ForeignKey(
            entity = File::class,
            parentColumns = [File.PATH, File.BOOKSHELF_ID],
            childColumns = [ReadLaterFile.FILE_PATH, ReadLaterFile.BOOKSHELF_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [ReadLaterFile.FILE_PATH, ReadLaterFile.BOOKSHELF_ID])]
)
internal data class ReadLaterFile(
    @ColumnInfo(FILE_PATH) val filePath: String,
    @ColumnInfo(BOOKSHELF_ID) val bookshelfId: Int,
) {
    companion object {
        const val FILE_PATH = "file_path"
        const val BOOKSHELF_ID = "bookshelf_id"
        fun fromModel(model: ReadLaterFileModel) =
            ReadLaterFile(model.path, model.bookshelfModelId.value)
    }
}
