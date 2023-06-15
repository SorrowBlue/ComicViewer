package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.entity.Favorite
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFileCount
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoriteDao {

    @Upsert
    suspend fun upsert(favorite: Favorite): Long

    @Delete
    suspend fun delete(favorite: Favorite): Int

    @Query("SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE favorite_id = :favoriteId) AS count FROM favorite WHERE id = :favoriteId")
    fun flow(favoriteId: Int): Flow<FavoriteFileCount?>

    @Query("SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE favorite_id = id) AS count FROM favorite")
    fun pagingSource(): PagingSource<Int, FavoriteFileCount>
}
