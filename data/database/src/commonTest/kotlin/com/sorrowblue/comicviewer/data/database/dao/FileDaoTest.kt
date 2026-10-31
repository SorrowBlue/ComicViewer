package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.sorrowblue.comicviewer.data.database.DatabaseTest
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Folder
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

    @Test
    fun pagingSourceFileSearch_whenExcludeFolder_excludesFolderEntitiesAndKeepsBooks() = runTest {
        val bookshelfEntity = factory.createBookshelfEntity().let { entity ->
            dao.upsert(entity).let {
                entity.copy(id = BookshelfId(it.toInt()))
            }
        }
        val parentPath = "/parent/path"
        val subFolderEntity = factory.createFileEntity(
            index = 0,
            bookshelfId = bookshelfEntity.id,
            parent = parentPath,
        ).copy(
            path = "$parentPath/subfolder",
            name = "subfolder",
            fileType = FileEntity.Type.FOLDER,
            fileTypeOrder = FileEntity.Type.FOLDER.order,
            sortIndex = 0,
        )
        val imageFolderEntity = factory.createFileEntity(
            index = 1,
            bookshelfId = bookshelfEntity.id,
            parent = parentPath,
        ).copy(
            path = "$parentPath/image_folder",
            name = "image_folder",
            fileType = FileEntity.Type.IMAGE_FOLDER,
            fileTypeOrder = FileEntity.Type.IMAGE_FOLDER.order,
            sortIndex = 1,
        )
        val bookFileEntity = factory.createFileEntity(
            index = 2,
            bookshelfId = bookshelfEntity.id,
            parent = parentPath,
        ).copy(
            path = "$parentPath/book.cbz",
            name = "book.cbz",
            fileType = FileEntity.Type.FILE,
            fileTypeOrder = FileEntity.Type.FILE.order,
            sortIndex = 2,
        )
        val fileDao = database.fileDao()
        fileDao.upsertAll(listOf(subFolderEntity, imageFolderEntity, bookFileEntity))

        val searchCondition = SearchCondition(
            range = SearchCondition.Range.InFolder(parentPath),
            sortType = SortType.Name(isAsc = true),
        )

        val pagingSource = fileDao.pagingSourceFileSearch(
            bookshelfId = bookshelfEntity.id,
            searchCondition = searchCondition,
            excludeFolder = true,
        )
        val pager = TestPager(PagingConfig(10), pagingSource)
        val result = pager.refresh() as PagingSource.LoadResult.Page

        assertContentEquals(
            listOf(imageFolderEntity.path, bookFileEntity.path),
            result.data.map { it.fileEntity.path },
        )
    }

    @Test
    fun flow_whenFolderEntity_populatesChildCount() = runTest {
        val bookshelfEntity = factory.createBookshelfEntity().let { entity ->
            dao.upsert(entity).let {
                entity.copy(id = BookshelfId(it.toInt()))
            }
        }
        val folderPath = "/parent/folder"
        val folderEntity = factory.createFileEntity(
            index = 0,
            bookshelfId = bookshelfEntity.id,
            parent = "/parent",
        ).copy(
            path = folderPath,
            name = "folder",
            fileType = FileEntity.Type.FOLDER,
            fileTypeOrder = FileEntity.Type.FOLDER.order,
        )
        val children = List(2) { index ->
            factory.createFileEntity(
                index = index + 1,
                bookshelfId = bookshelfEntity.id,
                parent = folderPath,
            ).copy(
                path = "$folderPath/child$index.cbz",
            )
        }
        val fileDao = database.fileDao()
        fileDao.upsertAll(listOf(folderEntity) + children)

        val result = fileDao.flow(bookshelfEntity.id.value, folderPath).first()
        assertEquals(2, (result?.toModel() as? Folder)?.count)
    }
}
