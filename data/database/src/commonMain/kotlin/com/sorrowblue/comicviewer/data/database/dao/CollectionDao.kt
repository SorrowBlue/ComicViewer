package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntity
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntityCount
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntityCountExist
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CollectionDao {

    @Query(
        """
            SELECT
              *,
              (
                CASE
                WHEN type = "smart" THEN (
                  SELECT
                    COUNT(*)
                  FROM
                    file
                  WHERE
                    bookshelf_id = collection.bookshelf_id
                    AND CASE
                      WHEN collection.show_hidden = false THEN (hidden = false AND name NOT LIKE '.%')
                      ELSE "" = ""
                    END
                    AND CASE
                      WHEN collection.`range` = "InFolder" THEN file.parent == collection.range_parent
                      WHEN collection.`range` = "SubFolder" THEN file.parent LIKE collection.range_parent||"%"
                      ELSE file.parent != ""
                    END
                    AND name LIKE ("%"||`query`||"%")
                    AND CASE
                      WHEN period = "None" THEN "" = ""
                      WHEN period = "Hour24" THEN last_modified > strftime('%s000', datetime('now', '-24 hours'))
                      WHEN period = "Week1" THEN last_modified > strftime('%s000', datetime('now', '-7 days'))
                      WHEN period = "Month1" THEN last_modified > strftime('%s000', datetime('now', '-1 months'))
                      ELSE "" = ""
                    END
                )
                ELSE (SELECT COUNT(*) FROM collection_file WHERE id = collection_id)
                END
              ) as count
            FROM collection ORDER BY created_at DESC
        """
    )
    fun pagingSource(): PagingSource<Int, CollectionEntityCount>

    @Query(
        """
            SELECT
              *,
              (
                CASE
                WHEN type = "smart" THEN (
                  SELECT
                    COUNT(*)
                  FROM
                    file
                  WHERE
                    bookshelf_id = collection.bookshelf_id
                    AND CASE
                      WHEN collection.show_hidden = false THEN (hidden = false AND name NOT LIKE '.%')
                      ELSE "" = ""
                    END
                    AND CASE
                      WHEN collection.`range` = "InFolder" THEN file.parent == collection.range_parent
                      WHEN collection.`range` = "SubFolder" THEN file.parent LIKE collection.range_parent||"%"
                      ELSE file.parent != ""
                    END
                    AND name LIKE ("%"||`query`||"%")
                    AND CASE
                      WHEN period = "None" THEN "" = ""
                      WHEN period = "Hour24" THEN last_modified > strftime('%s000', datetime('now', '-24 hours'))
                      WHEN period = "Week1" THEN last_modified > strftime('%s000', datetime('now', '-7 days'))
                      WHEN period = "Month1" THEN last_modified > strftime('%s000', datetime('now', '-1 months'))
                      ELSE "" = ""
                    END
                )
                ELSE (SELECT COUNT(*) FROM collection_file WHERE id = collection_id)
                END
              ) as count,
              EXISTS(
                SELECT file_path
                FROM collection_file
                WHERE id = collection_id AND bookshelf_id = :bookshelfId AND file_path = :path
              ) exist
            FROM collection ORDER BY created_at DESC
        """
    )
    fun pagingSource(bookshelfId: BookshelfId, path: String): PagingSource<Int, CollectionEntityCountExist>

    @Query(
        """
            SELECT
              *,
              (
                CASE
                WHEN type = "smart" THEN (
                  SELECT
                    COUNT(*)
                  FROM
                    file
                  WHERE
                    bookshelf_id = collection.bookshelf_id
                    AND CASE
                      WHEN collection.show_hidden = false THEN (hidden = false AND name NOT LIKE '.%')
                      ELSE "" = ""
                    END
                    AND CASE
                      WHEN collection.`range` = "InFolder" THEN file.parent == collection.range_parent
                      WHEN collection.`range` = "SubFolder" THEN file.parent LIKE collection.range_parent||"%"
                      ELSE file.parent != ""
                    END
                    AND name LIKE ("%"||`query`||"%")
                    AND CASE
                      WHEN period = "None" THEN "" = ""
                      WHEN period = "Hour24" THEN last_modified > strftime('%s000', datetime('now', '-24 hours'))
                      WHEN period = "Week1" THEN last_modified > strftime('%s000', datetime('now', '-7 days'))
                      WHEN period = "Month1" THEN last_modified > strftime('%s000', datetime('now', '-1 months'))
                      ELSE "" = ""
                    END
                )
                ELSE (SELECT COUNT(*) FROM collection_file WHERE id = collection_id)
                END
              ) as count,
              EXISTS(
                SELECT file_path
                FROM collection_file
                WHERE id = collection_id AND bookshelf_id = :bookshelfId AND file_path = :path
              ) exist
            FROM collection ORDER BY updated_at DESC
        """
    )
    fun pagingSourceRecent(bookshelfId: BookshelfId, path: String): PagingSource<Int, CollectionEntityCountExist>

    @Query(
        """
            INSERT INTO collection
              (name, type, bookshelf_id, `query`, `range`, range_parent, period, sort_type, sort_type_asc, show_hidden)
            VALUES
              (:name, :type, :bookshelfId, :query, :range, :rangeParent, :period, :sortType, :sortTypeAsc, :showHidden)
        """
    )
    suspend fun insert(
        name: String,
        type: CollectionEntity.Type,
        bookshelfId: BookshelfId?,
        query: String?,
        range: String?,
        rangeParent: String?,
        period: String?,
        sortType: String?,
        sortTypeAsc: Boolean?,
        showHidden: Boolean?,
    ): Long

    @Query(
        """
            UPDATE collection
              SET name = :name,
                  type = :type,
                  bookshelf_id = null,
                  `query` = null,
                  `range` = null,
                  range_parent = null,
                  period = null,
                  sort_type = null,
                  sort_type_asc = null,
                  show_hidden = null,
                  updated_at = CURRENT_TIMESTAMP
            WHERE id = :id
        """
    )
    suspend fun update(
        id: CollectionId,
        name: String,
        type: CollectionEntity.Type = CollectionEntity.Type.Basic,
    ): Int

    @Query(
        """
            UPDATE collection
              SET name = :name,
                  type = :type,
                  bookshelf_id = :bookshelfId,
                  `query` = :query,
                  `range` = :range,
                  range_parent = :rangeParent,
                  period = :period,
                  sort_type = :sortType,
                  sort_type_asc = :sortTypeAsc,
                  show_hidden = :showHidden,
                  updated_at = CURRENT_TIMESTAMP
            WHERE id = :id
        """
    )
    suspend fun update(
        id: CollectionId,
        name: String,
        bookshelfId: BookshelfId?,
        query: String,
        range: String,
        rangeParent: String,
        period: String,
        sortType: String,
        sortTypeAsc: Boolean,
        showHidden: Boolean,
        type: CollectionEntity.Type = CollectionEntity.Type.Smart,
    ): Int

    @Query("DELETE FROM collection WHERE id = :id")
    suspend fun delete(id: CollectionId): Int

    @Query(
        """
            SELECT
              *,
              (
                CASE
                WHEN type = "smart" THEN (
                  SELECT
                    COUNT(*)
                  FROM
                    file
                  WHERE
                    bookshelf_id = collection.bookshelf_id
                    AND CASE
                      WHEN collection.show_hidden = false THEN (hidden = false AND name NOT LIKE '.%')
                      ELSE "" = ""
                    END
                    AND CASE
                      WHEN collection.`range` = "InFolder" THEN file.parent == collection.range_parent
                      WHEN collection.`range` = "SubFolder" THEN file.parent LIKE collection.range_parent||"%"
                      ELSE file.parent != ""
                    END
                    AND name LIKE ("%"||`query`||"%")
                    AND CASE
                      WHEN period = "None" THEN "" = ""
                      WHEN period = "Hour24" THEN last_modified > strftime('%s000', datetime('now', '-24 hours'))
                      WHEN period = "Week1" THEN last_modified > strftime('%s000', datetime('now', '-7 days'))
                      WHEN period = "Month1" THEN last_modified > strftime('%s000', datetime('now', '-1 months'))
                      ELSE "" = ""
                    END
                )
                ELSE (SELECT COUNT(*) FROM collection_file WHERE id = collection_id)
                END
              ) as count
            FROM
              collection
            WHERE
              id = :id
        """
    )
    fun flow(id: CollectionId): Flow<CollectionEntityCount?>

    @Query("SELECT * FROM collection")
    fun flowAll(): Flow<List<CollectionEntity>>
}

internal suspend fun CollectionDao.insert(entity: CollectionEntity): Long {
    return insert(
        name = entity.name,
        type = entity.type,
        bookshelfId = entity.bookshelfId,
        query = entity.searchCondition?.query,
        range = entity.searchCondition?.range?.name,
        rangeParent = entity.searchCondition?.rangeParent,
        period = entity.searchCondition?.period?.name,
        sortType = entity.searchCondition?.sortType?.name,
        sortTypeAsc = entity.searchCondition?.sortTypeAsc,
        showHidden = entity.searchCondition?.showHidden,
    )
}

internal suspend fun CollectionDao.update(entity: CollectionEntity) {
    when (entity.type) {
        CollectionEntity.Type.Basic -> {
            update(
                id = entity.id,
                name = entity.name,
            )
        }

        CollectionEntity.Type.Smart -> {
            checkNotNull(entity.searchCondition)
            update(
                id = entity.id,
                name = entity.name,
                bookshelfId = entity.bookshelfId,
                query = entity.searchCondition.query,
                range = entity.searchCondition.range.name,
                rangeParent = entity.searchCondition.rangeParent,
                period = entity.searchCondition.period.name,
                sortType = entity.searchCondition.sortType.name,
                sortTypeAsc = entity.searchCondition.sortTypeAsc,
                showHidden = entity.searchCondition.showHidden,
            )
        }
    }
}
