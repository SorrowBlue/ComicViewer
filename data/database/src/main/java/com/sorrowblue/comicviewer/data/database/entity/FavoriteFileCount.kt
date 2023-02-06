package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.Embedded

internal data class FavoriteFileCount(
    @Embedded val favorite: Favorite,
    val count: Int
)
