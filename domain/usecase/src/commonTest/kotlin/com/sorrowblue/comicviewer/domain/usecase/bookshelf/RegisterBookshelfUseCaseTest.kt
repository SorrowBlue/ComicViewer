/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.cache.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.cache.ImageCache
import com.sorrowblue.comicviewer.domain.model.cache.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReader
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest

@OptIn(InternalDataApi::class)
class RegisterBookshelfUseCaseTest {

    @Test
    fun registerNewBookshelf_fetchesRemoteFileOnceAndSavesRootWithNewBookshelfId() = runTest {
        val initialBookshelf = DeviceStorage(displayName = "NewShelf")
        val assignedId = BookshelfId(42)
        val path = "/storage/emulated/0/Manga"

        var clientCreatedCount = 0
        var fileFetchedCount = 0
        var savedFile: File? = null

        val remoteClientFactory = object : RemoteStorageClient.Factory {
            override fun create(bookshelf: Bookshelf): RemoteStorageClient {
                clientCreatedCount++
                return object : TestRemoteStorageClient() {
                    override suspend fun connect(path: String) = Unit

                    override suspend fun file(path: String, resolveImageFolder: Boolean): File {
                        fileFetchedCount++
                        return Folder(
                            bookshelfId = bookshelf.id,
                            name = "Manga",
                            parent = "/storage/emulated/0",
                            path = path,
                            size = 0,
                            lastModifier = 100L,
                            isHidden = false,
                        )
                    }
                }
            }
        }

        val bookshelfRepository = object : TestRegisterBookshelfRepository() {
            override suspend fun updateOrCreate(
                bookshelf: Bookshelf,
                transaction: suspend (Bookshelf) -> Unit,
            ): Bookshelf {
                val created = DeviceStorage(
                    id = assignedId,
                    displayName = bookshelf.displayName,
                    fileCount = bookshelf.fileCount,
                    isDeleted = bookshelf.isDeleted,
                )
                transaction(created)
                return created
            }
        }

        val fileRepository = object : TestRegisterFileRepository() {
            override suspend fun root(id: BookshelfId): Folder? = null
            override suspend fun addUpdate(fileModel: File) {
                savedFile = fileModel
            }
        }

        val imageCacheRepository = object : ImageCacheRepository {
            override suspend fun deleteThumbnails(list: List<String>) = Unit
            override suspend fun clearImageCache() = Unit
            override suspend fun clearImageCache(
                bookshelfId: BookshelfId,
                imageCache: ImageCache,
            ): Resource<Unit, Resource.SystemError> = Resource.Success(Unit)

            override fun getBookshelfImageCacheInfo(
                bookshelf: Bookshelf,
            ): Resource<BookshelfImageCacheInfo, Resource.SystemError> = TODO()

            override fun getOtherImageCache(): Resource<OtherImageCache, Resource.SystemError> =
                TODO()
        }

        val useCase = RegisterBookshelfUseCaseImpl(
            fileRepository = fileRepository,
            bookshelfRepository = bookshelfRepository,
            remoteStorageClientFactory = remoteClientFactory,
            imageCacheRepository = imageCacheRepository,
        )

        val result = useCase(RegisterBookshelfUseCase.Request(initialBookshelf, path))

        assertTrue(result is Resource.Success)
        assertEquals(assignedId, result.data.id)
        assertEquals(1, clientCreatedCount)
        assertEquals(1, fileFetchedCount)
        val rootFolder = savedFile as Folder
        assertEquals(assignedId, rootFolder.bookshelfId)
        assertEquals("", rootFolder.parent)
        assertEquals(path, rootFolder.path)
    }
}

private open class TestRegisterBookshelfRepository : BookshelfRepository {
    override suspend fun updateOrCreate(
        bookshelf: Bookshelf,
        transaction: suspend (Bookshelf) -> Unit,
    ): Bookshelf? = TODO()

    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = TODO()
    override fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>> =
        TODO()

    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> = TODO()
    override suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean): Unit = TODO()
}

private open class TestRegisterFileRepository : FileRepository {
    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> = TODO()

    override fun pagingSourceBookThumbnail(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<BookThumbnail>> = TODO()

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId?,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> = TODO()

    override suspend fun addUpdate(fileModel: File): Unit = TODO()
    override suspend fun updateHistory(
        path: String,
        bookshelfId: BookshelfId,
        lastReadPage: Int,
        lastReading: Long,
    ): Unit = TODO()

    override suspend fun updateAdditionalInfo(
        path: String,
        bookshelfId: BookshelfId,
        cacheKey: String,
        totalPage: Int,
    ): Unit = TODO()

    override suspend fun updateSimple(list: File): Resource<File, FileRepositoryQueryError> = TODO()
    override fun pagingSource(
        bookshelfId: BookshelfId,
        pagingConfig: PagingConfig,
    ): Flow<PagingData<BookThumbnail>> = TODO()

    override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = TODO()
    override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = TODO()
    override fun nextFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> = TODO()

    override fun nextFileModel(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType,
    ): Flow<File?> = TODO()

    override fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> = TODO()

    override fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType,
    ): Flow<File?> = TODO()

    override suspend fun getCacheKeys(
        bookshelfId: BookshelfId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrder,
    ): List<String> = TODO()

    override suspend fun removeCacheKey(diskCacheKey: String): Unit = TODO()
    override suspend fun root(id: BookshelfId): Folder? = TODO()
    override fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<Book>> =
        TODO()

    override suspend fun clearCacheKey(bookshelfId: BookshelfId): Unit = TODO()
    override suspend fun deleteAllHistory(): Unit = TODO()
    override suspend fun updateHistory(file: File, files: List<File>): Unit = TODO()
    override suspend fun deleteAll(bookshelfModelId: BookshelfId): Unit = TODO()
    override suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String> = TODO()
    override fun lastHistory(): Flow<File?> = TODO()
    override suspend fun fileList(bookshelfId: BookshelfId, limit: Int, offset: Long): List<File> =
        TODO()

    override suspend fun count(bookshelfId: BookshelfId): Long = TODO()
    override suspend fun updateFileType(file: File): Unit = TODO()
}

private open class TestRemoteStorageClient : RemoteStorageClient {
    override suspend fun connect(path: String): Unit = TODO()
    override suspend fun exists(path: String): Boolean = TODO()
    override suspend fun listFiles(
        file: File,
        resolveImageFolder: Boolean,
        filter: (File) -> Boolean,
    ): List<File> = TODO()

    override suspend fun file(path: String, resolveImageFolder: Boolean): File = TODO()
    override suspend fun pageCount(book: Book): Int = TODO()
    override suspend fun fileReader(book: Book): BookFileReader = TODO()
    override suspend fun getAttribute(path: String): FileAttribute? = TODO()
    override suspend fun getFileSize(path: String): Long = TODO()
}
