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
import com.sorrowblue.comicviewer.data.database.entity.Server
import com.sorrowblue.comicviewer.data.database.entity.ServerFile

@Dao
internal interface ServerDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(server: Server): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(server: Server): Int

    @Upsert
    suspend fun upsert(server: Server): Long

    @Delete
    suspend fun delete(server: Server): Int

    @Query("SELECT * FROM server WHERE id = :serverId")
    suspend fun selectById(serverId: Int): Server?

    @Transaction
    suspend fun insertOrUpdate(server: Server): Int {
        return if (selectById(server.id) != null) {
            update(server)
            server.id
        } else {
            insert(server).toInt()
        }
    }

    @Query("SELECT s.*, f.* FROM server s LEFT OUTER JOIN file f ON s.id = f.server_id AND f.parent = '' ORDER BY s.id")
    fun pagingSource(): PagingSource<Int, ServerFile>
}
