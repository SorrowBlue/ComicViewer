package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FavoriteModelId
import com.sorrowblue.comicviewer.data.common.ReadLaterModel
import com.sorrowblue.comicviewer.data.common.ServerModelId

@Entity(
    tableName = "read_later",
    primaryKeys = [FILE_PATH, SERVER_ID],
    foreignKeys = [
        ForeignKey(
            entity = File::class,
            parentColumns = [SERVER_ID, "path"],
            childColumns = [SERVER_ID, FILE_PATH],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [SERVER_ID, FILE_PATH])]
)
internal data class ReadLater(
    @ColumnInfo(SERVER_ID) val serverId: Int,
    @ColumnInfo(FILE_PATH) val filePath: String,
) {

    fun toModel(): ReadLaterModel {
        return ReadLaterModel(ServerModelId(serverId), filePath)
    }
}


@Entity(tableName = "favorite")
internal data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
) {

    fun toModel(): FavoriteModel {
        return FavoriteModel(FavoriteModelId(id), name, 0)
    }
}

internal const val FILE_PATH = "file_path"
internal const val FAVORITE_ID = "favorite_id"

internal data class FavoriteAndBookCount(
    @Embedded val favorite: Favorite,
    val count: Int
)
