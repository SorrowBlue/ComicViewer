package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite

@Entity(tableName = "favorite")
internal data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(ID)
    val id: Int,
    val name: String,
) {

    companion object {

        const val ID = "id"

        fun fromModel(model: Favorite) = FavoriteEntity(id = model.id.value, name = model.name)
    }
}
