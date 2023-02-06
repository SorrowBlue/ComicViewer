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
    suspend fun delete(favorite: Favorite)

    @Query("SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE favorite_id = :favoriteId) AS count FROM favorite WHERE id = :favoriteId")
    suspend fun findBy(favoriteId: Int): FavoriteFileCount?

    @Query("SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE favorite_id = :favoriteId) AS count FROM favorite WHERE id = :favoriteId")
    fun findByAsFlow(favoriteId: Int): Flow<FavoriteFileCount?>

    @Query("SELECT favorite.* FROM favorite_file INNER JOIN favorite ON favorite.id = favorite_file.favorite_id WHERE favorite_file.bookshelf_id = :bookshelfId AND favorite_file.file_path = :filePath")
    fun selectBy(bookshelfId: Int, filePath: String): List<Favorite>

    @Query("SELECT * FROM favorite")
    fun pagingSource(): PagingSource<Int, Favorite>

    @Query("SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE favorite_id = id) AS count FROM favorite")
    fun pagingSourceCount(): PagingSource<Int, FavoriteFileCount>
}
