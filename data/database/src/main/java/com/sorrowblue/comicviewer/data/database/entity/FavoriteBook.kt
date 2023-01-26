package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.sorrowblue.comicviewer.data.common.FavoriteBookModel
import com.sorrowblue.comicviewer.data.common.FavoriteModelId
import com.sorrowblue.comicviewer.data.common.ServerModelId

@Entity(
    tableName = "favorite_book",
    primaryKeys = [FAVORITE_ID, FILE_PATH, SERVER_ID],
    foreignKeys = [
        ForeignKey(
            entity = Favorite::class,
            parentColumns = ["id"],
            childColumns = [FAVORITE_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = File::class,
            parentColumns = [SERVER_ID, "path"],
            childColumns = [SERVER_ID, FILE_PATH],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [FAVORITE_ID, SERVER_ID, FILE_PATH])]
)
internal data class FavoriteBook(
    @ColumnInfo(FAVORITE_ID) val favoriteId: Int,
    @ColumnInfo(SERVER_ID) val serverId: Int,
    @ColumnInfo(FILE_PATH) val filePath: String
) {

    fun toModel(): FavoriteBookModel {
        return FavoriteBookModel(FavoriteModelId(favoriteId), ServerModelId(serverId), filePath)
    }
}
