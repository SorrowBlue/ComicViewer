package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.EmbeddedBookshelfFileCountEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the bookshelf table. Provides methods for
 * inserting, updating, deleting, and querying bookshelf data.
 */
@Dao
internal abstract class BookshelfDao(val database: ComicViewerDatabase) {
    /**
     * Insert or update a bookshelf entity.
     *
     * @param entity The bookshelf entity to insert or update.
     * @return The row ID of the inserted or updated entity.
     */
    @Upsert
    abstract suspend fun upsert(entity: BookshelfEntity): Long

    /**
     * Delete a bookshelf by its ID.
     *
     * @param bookshelfId The ID of the bookshelf to delete.
     * @return The number of rows deleted.
     */
    @Query("DELETE FROM bookshelf WHERE id = :bookshelfId")
    abstract suspend fun delete(bookshelfId: Int): Int

    /**
     * Update the deleted flag for a bookshelf.
     *
     * @param bookshelfId The ID of the bookshelf to update.
     * @param deleted The deleted flag value (0: not deleted, 1: deleted).
     */
    @Query("UPDATE bookshelf SET deleted = :deleted WHERE id = :bookshelfId")
    abstract suspend fun updateDeleted(bookshelfId: Int, deleted: Int)

    /**
     * Get a bookshelf entity by its ID as a Flow.
     *
     * @param bookshelfId The ID of the bookshelf to query.
     * @return A Flow emitting the bookshelf entity, or null if not found.
     */
    @Query("SELECT * FROM bookshelf WHERE id = :bookshelfId")
    abstract fun flow(bookshelfId: Int): Flow<BookshelfEntity?>

    /**
     * Get all non-deleted bookshelves and their root files with file count, as
     * a PagingSource.
     *
     * @return PagingSource for EmbeddedBookshelfFileCountEntity.
     */
    @Query(
        "SELECT bookshelf.*, file.*, (SELECT COUNT(*) FROM file file2 WHERE bookshelf.id = file2.bookshelf_id AND file2.file_type = 'FILE') file_count FROM (SELECT * FROM bookshelf WHERE bookshelf.deleted = 0) bookshelf LEFT OUTER JOIN file ON bookshelf.id = file.bookshelf_id AND file.parent = '' ORDER BY bookshelf.id",
    )
    abstract fun pagingSourceNoDeleted(): PagingSource<Int, EmbeddedBookshelfFileCountEntity>

    /**
     * Get all bookshelf entities as a Flow.
     *
     * @return A Flow emitting a list of all bookshelf entities.
     */
    @Query("SELECT * FROM bookshelf ORDER BY ID")
    abstract fun allBookshelf(): Flow<List<BookshelfEntity>>
}
