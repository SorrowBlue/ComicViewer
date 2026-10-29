package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.sorrowblue.comicviewer.data.database.DatabaseTest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.test.MediumTest
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

@MediumTest
@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class FileDaoTest : DatabaseTest() {
    private val dao: BookshelfDao get() = database.bookshelfDao()

    @Test
    fun pagingSourceHistoryTest() = runTest {
        val bookshelfEntity = factory.createBookshelfEntity().let { entity ->
            dao.upsert(entity).let {
                entity.copy(id = BookshelfId(it.toInt()))
            }
        }

        val fileEntities = List(10) {
            factory.createFileEntity(
                index = it,
                bookshelfId = bookshelfEntity.id,
                lastReadTime = 10L - it,
            )
        }
        val dao = database.fileDao()
        dao.upsertAll(fileEntities)

        val pagingSource = dao.pagingSourceHistory()
        val pager = TestPager(PagingConfig(10), pagingSource)
        val result = pager.refresh() as PagingSource.LoadResult.Page

        assertContentEquals(result.data, fileEntities)
    }

    @Test
    fun flowPrevNextFile_whenSameSize_breaksTiesBySortIndexWithoutLooping() = runTest {
        val bookshelfEntity = factory.createBookshelfEntity().let { entity ->
            dao.upsert(entity).let {
                entity.copy(id = BookshelfId(it.toInt()))
            }
        }

        val fileEntities = List(3) { index ->
            factory.createFileEntity(
                index = index,
                bookshelfId = bookshelfEntity.id,
                parent = "/parent/path",
            ).copy(
                size = 1024L,
                sortIndex = index,
            )
        }
        val fileDao = database.fileDao()
        fileDao.upsertAll(fileEntities)

        val searchCondition = SearchCondition(
            range = SearchCondition.Range.InFolder("/parent/path"),
            sortType = SortType.Size(isAsc = true),
        )

        val nextFrom0 = fileDao.flowPrevNextFile(
            bookshelfId = bookshelfEntity.id,
            path = fileEntities[0].path,
            searchCondition = searchCondition,
            isNext = true,
            sortType = SortType.Size(isAsc = true),
        ).first().firstOrNull()
        assertEquals(fileEntities[1].path, nextFrom0?.path)

        val nextFrom1 = fileDao.flowPrevNextFile(
            bookshelfId = bookshelfEntity.id,
            path = fileEntities[1].path,
            searchCondition = searchCondition,
            isNext = true,
            sortType = SortType.Size(isAsc = true),
        ).first().firstOrNull()
        assertEquals(fileEntities[2].path, nextFrom1?.path)
    }
}
