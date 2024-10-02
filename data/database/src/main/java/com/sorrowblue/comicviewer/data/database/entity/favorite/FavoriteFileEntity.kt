package com.sorrowblue.comicviewer.data.database.entity.favorite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId

@Entity(
    tableName = "favorite_file",
    primaryKeys = [FavoriteFileEntity.FAVORITE_ID, FavoriteFileEntity.FILE_PATH, FavoriteFileEntity.BOOKSHELF_ID],
    foreignKeys = [
        ForeignKey(
            entity = FavoriteEntity::class,
            parentColumns = [FavoriteEntity.ID],
            childColumns = [FavoriteFileEntity.FAVORITE_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = [FileEntity.PATH, FileEntity.BOOKSHELF_ID],
            childColumns = [FavoriteFileEntity.FILE_PATH, FavoriteFileEntity.BOOKSHELF_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [FavoriteFileEntity.FILE_PATH, FavoriteFileEntity.BOOKSHELF_ID])]
)
internal data class FavoriteFileEntity(
    @ColumnInfo(FAVORITE_ID) val favoriteId: FavoriteId,
    @ColumnInfo(FILE_PATH) val filePath: String,
    @ColumnInfo(BOOKSHELF_ID) val bookshelfId: BookshelfId,
) {

    companion object {
        const val FAVORITE_ID = "favorite_id"
        const val BOOKSHELF_ID = "bookshelf_id"
        const val FILE_PATH = "file_path"
        fun fromModel(model: FavoriteFile) = FavoriteFileEntity(
            favoriteId = model.id,
            filePath = model.path,
            bookshelfId = model.bookshelfId
        )
    }
}
