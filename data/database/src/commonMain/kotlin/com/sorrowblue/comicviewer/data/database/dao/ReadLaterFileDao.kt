package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Upsert
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.readlater.ReadLaterFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
internal interface ReadLaterFileDao {
    @Upsert
    suspend fun upsert(readLaterFileEntity: ReadLaterFileEntity): Long

    @Delete
    suspend fun delete(readLaterFileEntity: ReadLaterFileEntity): Int

    @Query("DELETE FROM read_later_file")
    suspend fun deleteAll(): Int

    @Query(
        "SELECT EXISTS(SELECT * FROM read_later_file WHERE bookshelf_id=:bookshelfId AND file_path=:path)",
    )
    fun exists(bookshelfId: Int, path: String): Flow<Boolean>

    @Query(
        "SELECT file.* FROM read_later_file INNER JOIN file ON read_later_file.bookshelf_id = file.bookshelf_id AND read_later_file.file_path = file.path ORDER BY read_later_file.modified_date ASC",
    )
    fun pagingSourceReadLaterFile(): PagingSource<Int, FileEntity>
}
