package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.sorrowblue.comicviewer.data.common.OrderModel
import com.sorrowblue.comicviewer.data.common.SortType
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.Server
import com.sorrowblue.comicviewer.data.database.entity.ServerFile

@Dao
internal interface ServerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(server: Server): Long

    @Update
    suspend fun update(server: Server)

    @Delete
    suspend fun delete(server: Server): Int


    @Query("SELECT * FROM server WHERE id = :serverId")
    suspend fun selectById(serverId: Int): Server?

    @Transaction
    suspend fun insertOrUpdate(server: Server): Long {
        return if (selectById(server.id) != null) {
            update(server)
            server.id.toLong()
        } else {
            insert(server)
        }
    }

    @Query("SELECT s.*, f.* FROM server s LEFT OUTER JOIN file f ON s.id = f.server_id AND f.parent = '' ORDER BY s.id")
    fun pagingSource(): PagingSource<Int, ServerFile>
}

internal fun FileDao.pagingSource(
    serverId: Int, parent: String, sortType: SortType
): PagingSource<Int, File> {
    val query =
        SupportSQLiteQueryBuilder.builder("file")
            .apply {
                columns(arrayOf("*"))
                selection("server_id = :serverId AND parent = :parent", arrayOf(serverId, parent))
                when (sortType) {
                    is SortType.NAME -> if (sortType.isAsc) "file_type_order, sort_index" else "file_type_order DESC, sort_index DESC"
                    is SortType.DATE -> if (sortType.isAsc) "file_type_order, last_modified, sort_index" else "file_type_order DESC, last_modified DESC, sort_index DESC"
                    is SortType.SIZE -> if (sortType.isAsc) "file_type_order, file_size, sort_index" else "file_type_order DESC, file_size DESC, sort_index DESC"
                }.let(::orderBy)
            }.create()
    @Suppress("DEPRECATION")
    return pagingSource(query)
}

internal fun FileDao.pagingSourceQuery(
    serverId: Int, q: String, sortType: SortType
): PagingSource<Int, File> {
    val query =
        SupportSQLiteQueryBuilder.builder("file")
            .apply {
                columns(arrayOf("*"))
                selection("server_id = :serverId AND name LIKE :path", arrayOf(serverId, "%$q%"))
                when (sortType) {
                    is SortType.NAME -> if (sortType.isAsc) "file_type_order, sort_index" else "file_type_order DESC, sort_index DESC"
                    is SortType.DATE -> if (sortType.isAsc) "file_type_order, last_modified, sort_index" else "file_type_order DESC, last_modified DESC, sort_index DESC"
                    is SortType.SIZE -> if (sortType.isAsc) "file_type_order, file_size, sort_index" else "file_type_order DESC, file_size DESC, sort_index DESC"
                }.let(::orderBy)
            }.create()
    @Suppress("DEPRECATION")
    return pagingSource(query)
}
