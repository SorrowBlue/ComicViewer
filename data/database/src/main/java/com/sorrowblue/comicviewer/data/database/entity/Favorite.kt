package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FavoriteModelId

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
