package com.sorrowblue.comicviewer.data.database.entity.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId

@Entity(
    tableName = "collection_file",
    primaryKeys = [
        CollectionFileEntity.COLLECTION_ID,
        CollectionFileEntity.BOOKSHELF_ID,
        CollectionFileEntity.FILE_PATH,
    ],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = [CollectionEntity.ID],
            childColumns = [CollectionFileEntity.COLLECTION_ID],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = [FileEntity.PATH, FileEntity.BOOKSHELF_ID],
            childColumns = [CollectionFileEntity.FILE_PATH, CollectionFileEntity.BOOKSHELF_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = [CollectionFileEntity.FILE_PATH, CollectionFileEntity.BOOKSHELF_ID])],
)
internal data class CollectionFileEntity(
    @ColumnInfo(COLLECTION_ID) val collectionId: CollectionId,
    @ColumnInfo(BOOKSHELF_ID) val bookshelfId: BookshelfId,
    @ColumnInfo(FILE_PATH) val filePath: String,
) {
    companion object {
        const val COLLECTION_ID = "collection_id"
        const val BOOKSHELF_ID = "bookshelf_id"
        const val FILE_PATH = "file_path"

        fun fromModel(model: CollectionFile) = CollectionFileEntity(
            collectionId = model.id,
            filePath = model.path,
            bookshelfId = model.bookshelfId,
        )
    }
}
