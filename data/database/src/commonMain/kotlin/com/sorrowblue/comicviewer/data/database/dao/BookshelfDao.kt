package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.EmbeddedBookshelfFileCountEntity
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface BookshelfDao {

    @Upsert
    suspend fun upsert(entity: BookshelfEntity): Long

    @Delete
    suspend fun delete(entity: BookshelfEntity): Int

    @Query("DELETE FROM bookshelf WHERE id = :bookshelfId")
    suspend fun delete(bookshelfId: Int): Int

    @Query("UPDATE bookshelf SET deleted = :deleted WHERE id = :bookshelfId")
    suspend fun updateDeleted(bookshelfId: Int, deleted: Int)

    @Query("SELECT * FROM bookshelf WHERE id = :bookshelfId")
    fun flow(bookshelfId: Int): Flow<BookshelfEntity?>

    @Query(
        "SELECT bookshelf.*, file.*, (SELECT COUNT(*) FROM file file2 WHERE bookshelf.id = file2.bookshelf_id AND file2.file_type = 'FILE') file_count FROM (SELECT * FROM bookshelf WHERE bookshelf.deleted = 0) bookshelf LEFT OUTER JOIN file ON bookshelf.id = file.bookshelf_id AND file.parent = '' ORDER BY bookshelf.id"
    )
    fun pagingSourceNoDeleted(): PagingSource<Int, EmbeddedBookshelfFileCountEntity>

    @Query("SELECT * FROM file WHERE bookshelf_id = :bookshelfId AND file_type != 'FOLDER'")
    fun pagingSourceFileOnBookshelf(bookshelfId: Int): PagingSource<Int, FileEntity>

    @Query("SELECT * FROM bookshelf")
    fun allBookshelf(): Flow<List<BookshelfEntity>>
}
