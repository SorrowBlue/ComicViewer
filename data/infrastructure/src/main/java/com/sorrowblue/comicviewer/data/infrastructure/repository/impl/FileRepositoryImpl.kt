package com.sorrowblue.comicviewer.data.infrastructure.repository.impl

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.ReadLaterFileModelLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.infrastructure.di.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.Scan
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.model.Unknown
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepositoryError
import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class FileRepositoryImpl @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val imageCacheDataSource: ImageCacheDataSource,
    private val fileScanService: FileScanService,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val settingsCommonRepository: SettingsCommonRepository,
    private val readLaterFileModelLocalDataSource: ReadLaterFileModelLocalDataSource,
) : FileRepository {

    override fun addReadLater(
        bookshelfId: BookshelfId,
        path: String,
    ): Flow<Resource<Unit, FileRepository.Error>> {
        return flow {
            readLaterFileModelLocalDataSource.add(ReadLaterFile(bookshelfId, path))
            emit(Resource.Success(Unit))
        }.flowOn(dispatcher)
    }

    override fun existsReadLater(
        bookshelfId: BookshelfId,
        path: String,
    ): Flow<Resource<Boolean, FileRepository.Error>> {
        return readLaterFileModelLocalDataSource.exists(ReadLaterFile(bookshelfId, path)).map {
            Resource.Success(it)
        }
    }

    override fun deleteReadLater(
        bookshelfId: BookshelfId,
        path: String,
    ): Flow<Resource<Unit, FileRepository.Error>> {
        return flow {
            readLaterFileModelLocalDataSource.delete(ReadLaterFile(bookshelfId, path))
            emit(Resource.Success(Unit))
        }.flowOn(dispatcher)
    }

    override fun deleteAllReadLater(): Flow<Resource<Unit, FileRepository.Error>> {
        return flow {
            readLaterFileModelLocalDataSource.deleteAll()
            emit(Resource.Success(Unit))
        }.flowOn(dispatcher)
    }

    override fun findByParent(
        bookshelfId: BookshelfId,
        parent: String,
    ): Flow<Resource<File, FileRepository.Error>> {
        return flow {
            emit(
                Resource.Success(
                    fileModelLocalDataSource.root(bookshelfId)!!
                )
            )
        }.flowOn(dispatcher)
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingSource(
            pagingConfig,
            bookshelf.id,
            searchCondition
        )
    }

    override fun find(
        bookshelfId: BookshelfId,
        path: String,
    ): Flow<Resource<File, FileRepository.Error>> {
        return flow {
            emit(Resource.Success(fileModelLocalDataSource.findBy(bookshelfId, path)!!))
        }.flowOn(dispatcher)
    }

    override suspend fun deleteThumbnails() {
        imageCacheDataSource.deleteThumbnails()
        fileModelLocalDataSource.deleteThumbnails()
    }

    override suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>) {
        fileModelLocalDataSource.deleteHistory(bookshelfId, list)
    }

    override suspend fun deleteAllDB(bookshelfId: BookshelfId) {
        fileModelLocalDataSource.deleteAll2(bookshelfId)
    }

    override suspend fun deleteAllCache(id: BookshelfId) {
        imageCacheDataSource.deleteThumbnails(
            fileModelLocalDataSource.getCacheKeyList(id)
        )
    }

    override suspend fun getBook(bookshelfId: BookshelfId, path: String): Response<Book?> {
        return Response.Success(
            fileModelLocalDataSource.findBy(bookshelfId, path) as? Book
        )
    }

    override fun getFile(bookshelfId: BookshelfId, path: String): Flow<Result<File, Unit>> {
        return kotlin.runCatching {
            fileModelLocalDataSource.flow(bookshelfId, path)
        }.fold({ fileModelFlow ->
            fileModelFlow.map {
                if (it != null) Result.Success(it) else Result.Error(Unit)
            }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }

    override suspend fun update(
        bookshelfId: BookshelfId,
        path: String,
        lastReadPage: Int,
        lastReadTime: Long,
    ) {
        fileModelLocalDataSource.updateHistory(path, bookshelfId, lastReadPage, lastReadTime)
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        folder: IFolder,
    ): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingSource(pagingConfig, bookshelf, folder) {
            val settings = runBlocking { settingsCommonRepository.folderDisplaySettings.first() }
            SearchCondition(
                "",
                SearchCondition.Range.InFolder(folder.path),
                SearchCondition.Period.NONE,
                when (settings.sortType) {
                    is SortType.DATE -> SearchCondition.Order.DATE
                    is SortType.NAME -> SearchCondition.Order.NAME
                    is SortType.SIZE -> SearchCondition.Order.SIZE
                },
                if (settings.sortType.isAsc) SearchCondition.Sort.ASC else SearchCondition.Sort.DESC
            )
        }
    }

    override suspend fun get(bookshelfId: BookshelfId, path: String): Response<File?> {
        return Response.Success(fileModelLocalDataSource.findBy(bookshelfId, path))
    }

    override suspend fun scan(folder: IFolder, scan: Scan): String {
        val folderSettings = settingsCommonRepository.folderSettings.first()
        return fileScanService.enqueue(
            folder,
            scan,
            folderSettings.resolveImageFolder,
            folderSettings.supportExtension.map(SupportExtension::extension)
        )
    }

    override suspend fun get2(bookshelfId: BookshelfId, path: String): Result<File?, Unit> {
        return kotlin.runCatching {
            fileModelLocalDataSource.findBy(bookshelfId, path)
        }.fold({
            Result.Success(it)
        }, {
            Result.Exception(Unknown(it))
        })
    }

    override suspend fun getRoot(bookshelfId: BookshelfId): Result<File?, Unit> {
        return kotlin.runCatching {
            fileModelLocalDataSource.root(bookshelfId)
        }.fold({
            Result.Success(it)
        }, {
            Result.Exception(Unknown(it))
        })
    }

    override suspend fun getFolder(
        bookshelf: Bookshelf,
        path: String,
    ): Result<Folder, FileRepositoryError> {
        return withContext(dispatcher) {
            kotlin.runCatching {
                remoteDataSourceFactory.create(bookshelf).file(path)
            }.fold({ file ->
                if (file is Folder) {
                    Result.Success(file)
                } else {
                    Result.Error(FileRepositoryError.PathDoesNotExist)
                }
            }, {
                Result.Error(FileRepositoryError.IncorrectServerInfo)
            })
        }
    }

    override fun findNextBook(
        bookshelfId: BookshelfId,
        path: String,
        isNext: Boolean,
    ): Flow<Result<File, Unit>> {
        val sortType =
            runBlocking { settingsCommonRepository.folderDisplaySettings.first() }.sortType
        return kotlin.runCatching {
            if (isNext) {
                fileModelLocalDataSource.nextFileModel(bookshelfId, path, sortType)
            } else {
                fileModelLocalDataSource.prevFileModel(bookshelfId, path, sortType)
            }
        }.fold({ modelFlow ->
            modelFlow.map { if (it != null) Result.Success(it) else Result.Error(Unit) }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }

    override fun pagingHistoryBookFlow(pagingConfig: PagingConfig): Flow<PagingData<Book>> {
        return fileModelLocalDataSource.pagingHistoryBookSource(pagingConfig)
    }

    override fun lastHistory(): Flow<File> {
        return fileModelLocalDataSource.lastHistory()
    }
}
