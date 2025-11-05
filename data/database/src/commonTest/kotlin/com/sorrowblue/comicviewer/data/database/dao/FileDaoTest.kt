package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.sorrowblue.comicviewer.data.database.DatabaseTest
import com.sorrowblue.comicviewer.data.database.entity.EntityFactory
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlinx.coroutines.test.runTest

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class FileDaoTest : DatabaseTest() {
    private val dao: BookshelfDao get() = db.bookshelfDao()

    @Test
    fun pagingSourceHistoryTest() = runTest {
        val factory = EntityFactory()
        val bookshelfEntity = factory.createBookshelfEntity().let { entity ->
            dao.upsert(entity).let {
                entity.copy(BookshelfId(it.toInt()))
            }
        }

        val fileEntities = List(10) {
            factory.createFileEntity(
                index = it,
                bookshelfId = bookshelfEntity.id,
                lastReadTime = 10L - it,
            )
        }
        val dao = db.fileDao()
        dao.upsertAll(fileEntities)

        val pagingSource = dao.pagingSourceHistory()
        val pager = TestPager(PagingConfig(10), pagingSource)
        val result = pager.refresh() as PagingSource.LoadResult.Page

        assertContentEquals(result.data, fileEntities)
    }
}
