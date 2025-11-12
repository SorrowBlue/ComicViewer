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
        CollectionFileEntity.CollectionId,
        CollectionFileEntity.BookshelfId,
        CollectionFileEntity.FilePath,
    ],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = [CollectionEntity.ID],
            childColumns = [CollectionFileEntity.CollectionId],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = [FileEntity.PATH, FileEntity.BookshelfId],
            childColumns = [CollectionFileEntity.FilePath, CollectionFileEntity.BookshelfId],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = [CollectionFileEntity.FilePath, CollectionFileEntity.BookshelfId])],
)
internal data class CollectionFileEntity(
    @ColumnInfo(CollectionId) val collectionId: CollectionId,
    @ColumnInfo(BookshelfId) val bookshelfId: BookshelfId,
    @ColumnInfo(FilePath) val filePath: String,
) {
    companion object {
        const val CollectionId = "collection_id"
        const val BookshelfId = "bookshelf_id"
        const val FilePath = "file_path"

        fun fromModel(model: CollectionFile) = CollectionFileEntity(
            collectionId = model.id,
            filePath = model.path,
            bookshelfId = model.bookshelfId,
        )
    }
}
