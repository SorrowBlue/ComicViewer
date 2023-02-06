package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteFileModel

@Entity(
    tableName = "favorite_file",
    primaryKeys = [FavoriteFile.FAVORITE_ID, FavoriteFile.FILE_PATH, FavoriteFile.BOOKSHELF_ID],
    foreignKeys = [
        ForeignKey(
            entity = Favorite::class,
            parentColumns = [Favorite.ID],
            childColumns = [FavoriteFile.FAVORITE_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = File::class,
            parentColumns = [File.PATH, File.BOOKSHELF_ID],
            childColumns = [FavoriteFile.FILE_PATH, FavoriteFile.BOOKSHELF_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [FavoriteFile.FAVORITE_ID, FavoriteFile.FILE_PATH, FavoriteFile.BOOKSHELF_ID])]
)
internal data class FavoriteFile(
    @ColumnInfo(FAVORITE_ID) val favoriteId: Int,
    @ColumnInfo(FILE_PATH) val filePath: String,
    @ColumnInfo(BOOKSHELF_ID) val serverId: Int
) {

    companion object {
        const val FAVORITE_ID = "favorite_id"
        const val BOOKSHELF_ID = "bookshelf_id"
        const val FILE_PATH = "file_path"
        fun fromModel(model: FavoriteFileModel) =
            FavoriteFile(model.id.value, model.filePath, model.bookshelfModelId.value)
    }
}
