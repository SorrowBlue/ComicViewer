package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.ReadLaterFile

@Dao
internal interface ReadLaterFileDao {

    @Upsert
    suspend fun upsert(readLaterFile: ReadLaterFile): Long

    @Query("SELECT file.* FROM read_later_file INNER JOIN file ON read_later_file.bookshelf_id = file.bookshelf_id AND read_later_file.file_path = file.path")
    fun pagingSource(): PagingSource<Int, File>
}
