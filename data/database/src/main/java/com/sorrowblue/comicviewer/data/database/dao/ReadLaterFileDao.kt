package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.ReadLaterFile
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ReadLaterFileDao {

    @Upsert
    suspend fun upsert(readLaterFile: ReadLaterFile): Long

    @Delete
    suspend fun delete(readLaterFile: ReadLaterFile)

    @Query("SELECT file.* FROM read_later_file INNER JOIN file ON read_later_file.bookshelf_id = file.bookshelf_id AND read_later_file.file_path = file.path")
    fun pagingSource(): PagingSource<Int, File>
}
