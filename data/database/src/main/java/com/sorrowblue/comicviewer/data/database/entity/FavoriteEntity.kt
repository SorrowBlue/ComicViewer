package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId

@Entity(tableName = "favorite")
internal data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(ID)
    val id: FavoriteId,
    val name: String,
) {

    companion object {

        const val ID = "id"

        fun fromModel(model: Favorite) = FavoriteEntity(id = model.id, name = model.name)
    }
}
