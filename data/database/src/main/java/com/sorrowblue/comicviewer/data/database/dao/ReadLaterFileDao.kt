package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.entity.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.ReadLaterFileEntity

@Dao
internal interface ReadLaterFileDao {

    @Upsert
    suspend fun upsert(readLaterFileEntity: ReadLaterFileEntity): Long

    @Delete
    suspend fun delete(readLaterFileEntity: ReadLaterFileEntity)

    @Query("SELECT file.* FROM read_later_file INNER JOIN file ON read_later_file.bookshelf_id = file.bookshelf_id AND read_later_file.file_path = file.path")
    fun pagingSource(): PagingSource<Int, FileEntity>

    @Query("DELETE FROM read_later_file")
    suspend fun deleteAll()
}
