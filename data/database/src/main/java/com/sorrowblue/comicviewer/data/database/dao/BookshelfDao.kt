package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.entity.Bookshelf
import com.sorrowblue.comicviewer.data.database.entity.BookshelfFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
internal interface BookshelfDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(bookshelf: Bookshelf): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(bookshelf: Bookshelf): Int

    @Upsert
    suspend fun upsert(bookshelf: Bookshelf): Long

    @Delete
    suspend fun delete(bookshelf: Bookshelf): Int

    @Query("SELECT * FROM bookshelf WHERE id = :bookshelfId")
    fun selectById(bookshelfId: Int): Flow<Bookshelf?>

    @Transaction
    suspend fun insertOrUpdate(bookshelf: Bookshelf): Int {
        return if (selectById(bookshelf.id).first() != null) {
            update(bookshelf)
            bookshelf.id
        } else {
            insert(bookshelf).toInt()
        }
    }

    @Query("SELECT s.*, f.* FROM bookshelf s LEFT OUTER JOIN file f ON s.id = f.bookshelf_id AND f.parent = '' ORDER BY s.id")
    fun pagingSource(): PagingSource<Int, BookshelfFile>
}
