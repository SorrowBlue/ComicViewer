package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.entity.FavoriteEntity
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFileCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoriteDao {

    @Upsert
    suspend fun upsert(favoriteEntity: FavoriteEntity): Long

    @Delete
    suspend fun delete(favoriteEntity: FavoriteEntity): Int

    @Query(
        "SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE favorite_id = :favoriteId) AS count FROM favorite WHERE id = :favoriteId"
    )
    fun flow(favoriteId: Int): Flow<FavoriteFileCountEntity?>

    @Query("SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE favorite_id = id) AS count FROM favorite")
    fun pagingSource(): PagingSource<Int, FavoriteFileCountEntity>
}
