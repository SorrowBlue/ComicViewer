package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.ReadLater

@Dao
internal interface ReadLaterDao {

    @Upsert
    suspend fun upsert(readLater: ReadLater): Long

    @Query("SELECT file.* FROM read_later INNER JOIN file ON read_later.server_id = file.server_id AND read_later.file_path = file.path")
    fun pagingSource(): PagingSource<Int, File>
}
