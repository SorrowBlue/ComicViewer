package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.sorrowblue.comicviewer.data.database.DatabaseTest
import com.sorrowblue.comicviewer.data.database.entity.EntityFactory
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPassword
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.EmbeddedBookshelfFileCountEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class BookshelfDaoTest : DatabaseTest() {
    private val dao: BookshelfDao get() = database.bookshelfDao()

    @Test
    fun upsert_and_flow() = runTest {
        val entity = BookshelfEntity(
            id = BookshelfId(1),
            displayName = "Test",
            type = BookshelfEntity.Type.INTERNAL,
            deleted = false,
            host = "",
            port = 0,
            domain = "",
            username = "",
            password = DecryptedPassword(""),
        )
        dao.upsert(entity)
        val result = dao.flow(1).first()
        assertEquals(entity, result)
    }

    @Test
    fun delete_by_id() = runTest {
        val entity = BookshelfEntity(
            id = BookshelfId(3),
            displayName = "DeleteId",
            type = BookshelfEntity.Type.INTERNAL,
            deleted = false,
            host = "",
            port = 0,
            domain = "",
            username = "",
            password = DecryptedPassword(""),
        )
        dao.upsert(entity)
        val deletedCount = dao.delete(3)
        assertEquals(1, deletedCount)
    }

    @Test
    fun updateDeleted() = runTest {
        val entity = BookshelfEntity(
            id = BookshelfId(4),
            displayName = "Update",
            type = BookshelfEntity.Type.INTERNAL,
            deleted = false,
            host = "",
            port = 0,
            domain = "",
            username = "",
            password = DecryptedPassword(""),
        )
        dao.upsert(entity)
        dao.updateDeleted(4, 1)
        val result = dao.flow(4).first()
        assertEquals(true, result?.deleted)
    }

    @Test
    fun pagingSourceNoDeletedTest() = runTest {
        val factory = EntityFactory()
        val bookshelfList = List(100) {
            factory.createBookshelfEntity(it + 1, deleted = it % 2 == 0)
        }
        val fileDao = database.fileDao()
        bookshelfList.forEach {
            dao.upsert(it)
            fileDao.upsert(factory.createFileEntity(1, it.id, ""))
        }

        val result = dao.allBookshelf().first()
        assertContentEquals(bookshelfList, result)

        val pagingSource = dao.pagingSourceNoDeleted()
        val pager = TestPager(PagingConfig(20), pagingSource)
        val page = with(pager) {
            refresh()
        } as PagingSource.LoadResult.Page
        assertContentEquals(
            page.data.map(EmbeddedBookshelfFileCountEntity::entity),
            bookshelfList.filterNot(BookshelfEntity::deleted),
        )
    }

    @Test
    fun allBookshelf() = runTest {
        val factory = EntityFactory()
        val bookshelfList = List(100) {
            factory.createBookshelfEntity(it + 1)
        }.apply {
            forEach { dao.upsert(it) }
        }
        val result = dao.allBookshelf().first()
        assertContentEquals(bookshelfList, result)
    }
}
