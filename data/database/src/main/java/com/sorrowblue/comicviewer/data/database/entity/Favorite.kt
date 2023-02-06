package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId

@Entity(tableName = "favorite")
internal data class Favorite(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(ID) val id: Int,
    val name: String
) {

    companion object {
        const val ID = "id"

        fun fromModel(model: FavoriteModel) = Favorite(model.id.value, model.name)
    }

    fun toModel(): FavoriteModel {
        return FavoriteModel(FavoriteModelId(id), name, 0)
    }
}
