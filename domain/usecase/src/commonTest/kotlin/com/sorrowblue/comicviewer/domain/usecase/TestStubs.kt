/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.cache.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.cache.ImageCache
import com.sorrowblue.comicviewer.domain.model.cache.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionCriteria
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.readlater.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import com.sorrowblue.comicviewer.domain.repository.ReadLaterFileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.repository.file.BookFileReader
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import kotlinx.coroutines.flow.Flow

internal open class StubImageCacheRepository : ImageCacheRepository {
    override suspend fun deleteThumbnails(list: List<String>) = Unit
    override suspend fun clearImageCache() = Unit
    override suspend fun clearImageCache(
        bookshelfId: BookshelfId,
        imageCache: ImageCache,
    ): Resource<Unit, Resource.SystemError> = Resource.Success(Unit)

    override fun getBookshelfImageCacheInfo(
        bookshelf: Bookshelf,
    ): Resource<BookshelfImageCacheInfo, Resource.SystemError> = TODO()

    override fun getOtherImageCache(): Resource<OtherImageCache, Resource.SystemError> = TODO()
}

internal open class StubBookshelfRepository : BookshelfRepository {
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

internal open class StubFileRepository : FileRepository {
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

internal open class StubRemoteStorageClient : RemoteStorageClient {
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

internal open class StubSettingsRepository : SettingsRepository {
    override val settings: Flow<Settings> get() = TODO()
    override suspend fun updateSettings(transform: suspend (Settings) -> Settings): Settings =
        TODO()

    override val displaySettings: Flow<DisplaySettings> get() = TODO()
    override suspend fun updateDisplaySettings(
        transform: suspend (DisplaySettings) -> DisplaySettings,
    ): DisplaySettings = TODO()

    override val viewerSettings: Flow<ViewerSettings> get() = TODO()
    override suspend fun updateViewerSettings(
        transform: suspend (ViewerSettings) -> ViewerSettings,
    ): ViewerSettings = TODO()

    override val bookSettings: Flow<BookSettings> get() = TODO()
    override suspend fun updateBookSettings(
        transform: suspend (BookSettings) -> BookSettings,
    ): BookSettings = TODO()

    override val folderDisplaySettings: Flow<FolderDisplaySettings> get() = TODO()
    override suspend fun updateFolderDisplaySettings(
        transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings,
    ): FolderDisplaySettings = TODO()

    override val folderSettings: Flow<FolderSettings> get() = TODO()
    override suspend fun updateFolderSettings(
        transform: suspend (FolderSettings) -> FolderSettings,
    ): FolderSettings = TODO()

    override val securitySettings: Flow<SecuritySettings> get() = TODO()
    override suspend fun updateSecuritySettings(
        transform: suspend (SecuritySettings) -> SecuritySettings,
    ): SecuritySettings = TODO()

    override val collectionSettings: Flow<CollectionSettings> get() = TODO()
    override suspend fun updateCollectionSettings(
        transform: suspend (CollectionSettings) -> CollectionSettings,
    ): CollectionSettings = TODO()
}

internal open class StubCollectionRepository : CollectionRepository {
    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Collection>> = TODO()
    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        path: String,
        collectionCriteria: () -> CollectionCriteria,
    ): Flow<PagingData<Pair<Collection, Boolean>>> = TODO()

    override fun flow(id: CollectionId): Flow<Collection?> = TODO()
    override suspend fun create(collection: Collection): Collection = TODO()
    override suspend fun update(collection: Collection): Unit = TODO()
    override suspend fun delete(collectionId: CollectionId): Unit = TODO()
}

internal open class StubCollectionFileRepository : CollectionFileRepository {
    override fun pagingDataFlow(
        id: CollectionId,
        pagingConfig: PagingConfig,
        sortType: () -> SortType,
    ): Flow<PagingData<File>> = TODO()

    override suspend fun add(file: CollectionFile): Unit = TODO()
    override suspend fun remove(file: CollectionFile): Unit = TODO()
    override fun flowNextCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?> =
        TODO()

    override fun flowPrevCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?> =
        TODO()

    override suspend fun getCacheKeyList(
        id: CollectionId,
        limit: Int,
    ): List<Pair<BookshelfId, String>> = TODO()
}

internal open class StubReadLaterFileRepository : ReadLaterFileRepository {
    override suspend fun updateOrAdd(
        file: ReadLaterFile,
    ): Resource<ReadLaterFile, Resource.SystemError> = TODO()

    override suspend fun delete(file: ReadLaterFile): Resource<Unit, Resource.SystemError> = TODO()
    override suspend fun deleteAll(): Resource<Unit, Resource.SystemError> = TODO()
    override fun exists(file: ReadLaterFile): Resource<Flow<Boolean>, Resource.SystemError> = TODO()
    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>> = TODO()
}

internal open class StubSendFatalErrorUseCase : SendFatalErrorUseCase() {
    val reportedThrowables = mutableListOf<Throwable>()
    override suspend fun run(request: Request): Resource<Unit, Unit> {
        reportedThrowables.add(request.throwable)
        return Resource.Success(Unit)
    }
}
