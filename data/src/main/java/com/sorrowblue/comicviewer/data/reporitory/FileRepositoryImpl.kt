package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ReadLaterFileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.SearchConditionEntity
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.common.model.ScanModel
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.data.datasource.ReadLaterFileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.mapper.from
import com.sorrowblue.comicviewer.data.mapper.toBookshelfModel
import com.sorrowblue.comicviewer.data.mapper.toFile
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.Scan
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.SupportExtension
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
import com.sorrowblue.comicviewer.framework.Resource
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class FileRepositoryImpl @Inject constructor(
    private val imageCacheDataSource: ImageCacheDataSource,
    private val fileScanService: FileScanService,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val settingsCommonRepository: SettingsCommonRepository,
    private val readLaterFileModelLocalDataSource: ReadLaterFileModelLocalDataSource
) : FileRepository {

    override fun addReadLater(
        bookshelfId: BookshelfId,
        path: String
    ): Flow<Resource<Unit, FileRepository.Error>> {
        return flow {
            readLaterFileModelLocalDataSource.add(ReadLaterFileModel(BookshelfModelId.from(bookshelfId), path))
            emit(Resource.Success(Unit))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteReadLater(
        bookshelfId: BookshelfId,
        path: String
    ): Flow<Resource<Unit, FileRepository.Error>> {
        return flow {
            readLaterFileModelLocalDataSource.delete(ReadLaterFileModel(BookshelfModelId.from(bookshelfId), path))
            emit(Resource.Success(Unit))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteAllReadLater(): Flow<Resource<Unit, FileRepository.Error>> {
        return flow {
            readLaterFileModelLocalDataSource.deleteAll()
            emit(Resource.Success(Unit))
        }.flowOn(Dispatchers.IO)
    }

    override fun findByParent(
        bookshelfId: BookshelfId,
        parent: String
    ): Flow<Resource<File, FileRepository.Error>> {
        return flow {
            emit(
                Resource.Success(
                    fileModelLocalDataSource.root(BookshelfModelId.from(bookshelfId))!!.toFile()
                )
            )
        }.flowOn(Dispatchers.IO)
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        searchCondition: () -> SearchCondition
    ): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingSource(
            pagingConfig,
            BookshelfModelId.from(bookshelf.id)
        ) {
            searchCondition().toEntity()
        }.map { pagingData -> pagingData.map(FileModel::toFile) }
    }

    override fun find(
        bookshelfId: BookshelfId,
        path: String
    ): Flow<Resource<File, FileRepository.Error>> {
        return flow {
            emit(
                Resource.Success(
                    fileModelLocalDataSource.findBy(
                        BookshelfModelId.from(bookshelfId),
                        path
                    )!!.toFile()
                )
            )
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun deleteThumbnails() {
        imageCacheDataSource.deleteThumbnails()
        fileModelLocalDataSource.deleteThumbnails()
    }

    override suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>) {
        fileModelLocalDataSource.deleteHistory(BookshelfModelId.from(bookshelfId), list)
    }

    override suspend fun deleteAllDB(bookshelfId: BookshelfId) {
        fileModelLocalDataSource.deleteAll2(BookshelfModelId.from(bookshelfId))
    }

    override suspend fun deleteAllCache(id: BookshelfId) {
        imageCacheDataSource.deleteThumbnails(fileModelLocalDataSource.getCacheKeyList(BookshelfModelId.from(id)))
    }

    override suspend fun getBook(bookshelfId: BookshelfId, path: String): Response<Book?> {
        return Response.Success(
            fileModelLocalDataSource.findBy(BookshelfModelId.from(bookshelfId), path)
                ?.toFile() as? Book
        )
    }

    override fun getFile(bookshelfId: BookshelfId, path: String): Flow<Result<File, Unit>> {
        return kotlin.runCatching {
            fileModelLocalDataSource.flow(BookshelfModelId.from(bookshelfId), path)
        }.fold({ fileModelFlow ->
            fileModelFlow.map {
                if (it != null) Result.Success(it.toFile()) else Result.Error(Unit)
            }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }

    override suspend fun update(
        bookshelfId: BookshelfId,
        path: String,
        lastReadPage: Int,
        lastReadTime: Long
    ) {
        fileModelLocalDataSource.updateHistory(
            path,
            BookshelfModelId.from(bookshelfId),
            lastReadPage,
            lastReadTime
        )
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        folder: IFolder
    ): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingSource(
            pagingConfig,
            BookshelfModel.from(bookshelf),
            FileModel.from(folder)
        ) {
            val settings = runBlocking { settingsCommonRepository.folderDisplaySettings.first() }
            SortEntity.from(settings.sortType)
            SearchConditionEntity(
                SearchConditionEntity.NO_QUERY,
                SearchConditionEntity.Range.InFolder(folder.path),
                SearchConditionEntity.Period.NONE,
                when (settings.sortType) {
                    is SortType.DATE -> SearchConditionEntity.Order.DATE
                    is SortType.NAME -> SearchConditionEntity.Order.NAME
                    is SortType.SIZE -> SearchConditionEntity.Order.SIZE
                },
                if (settings.sortType.isAsc) SearchConditionEntity.Sort.ASC else SearchConditionEntity.Sort.DESC
            )
        }.map { it.map(FileModel::toFile) }
    }

    override suspend fun get(bookshelfId: BookshelfId, path: String): Response<File?> {
        return Response.Success(
            fileModelLocalDataSource.findBy(BookshelfModelId.from(bookshelfId), path)?.toFile()
        )
    }

    override suspend fun scan(folder: IFolder, scan: Scan): String {
        val folderSettings = settingsCommonRepository.folderSettings.first()
        return fileScanService.enqueue(
            FileModel.from(folder),
            when (scan) {
                Scan.ALL -> ScanModel.ALL
                Scan.IN_FOLDER -> ScanModel.IN_FOLDER
                Scan.IN_FOLDER_SUB -> ScanModel.IN_FOLDER_SUB
            },
            folderSettings.resolveImageFolder,
            folderSettings.supportExtension.map(SupportExtension::extension)
        )
    }

    override suspend fun get2(bookshelfId: BookshelfId, path: String): Result<File?, Unit> {
        return kotlin.runCatching {
            fileModelLocalDataSource.findBy(BookshelfModelId.from(bookshelfId), path)
        }.fold({
            Result.Success(it?.toFile())
        }, {
            Result.Exception(Unknown(it))
        })
    }

    override suspend fun getRoot(bookshelfId: BookshelfId): Result<File?, Unit> {
        return kotlin.runCatching {
            fileModelLocalDataSource.root(BookshelfModelId.from(bookshelfId))
        }.fold({
            Result.Success(it?.toFile())
        }, {
            Result.Exception(Unknown(it))
        })
    }

    override suspend fun getFolder(
        bookshelf: Bookshelf,
        path: String
    ): Result<Folder, FileRepositoryError> {
        return withContext(Dispatchers.IO) {
            kotlin.runCatching {
                remoteDataSourceFactory.create(bookshelf.toBookshelfModel()).fileModel(path).toFile()
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

    override fun getNextRelFile(
        bookshelfId: BookshelfId,
        path: String,
        isNext: Boolean
    ): Flow<Result<File, Unit>> {
        val sortEntity = runBlocking { settingsCommonRepository.folderDisplaySettings.first() }.sortType.let(SortEntity.Companion::from)
        return kotlin.runCatching {
            if (isNext) {
                fileModelLocalDataSource.nextFileModel(BookshelfModelId.from(bookshelfId), path, sortEntity)
            } else {
                fileModelLocalDataSource.prevFileModel(BookshelfModelId.from(bookshelfId), path, sortEntity)
            }
        }.fold({ modelFlow ->
            modelFlow.map { if (it != null) Result.Success(it.toFile()) else Result.Error(Unit) }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }

    override fun pagingHistoryBookFlow(pagingConfig: PagingConfig): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingHistoryBookSource(pagingConfig)
            .map { pagingData -> pagingData.map(FileModel::toFile) }
    }
}

internal fun SortEntity.Companion.from(sortType: SortType): SortEntity {
    return when (sortType) {
        is SortType.DATE -> SortEntity.DATE(sortType.isAsc)
        is SortType.NAME -> SortEntity.NAME(sortType.isAsc)
        is SortType.SIZE -> SortEntity.SIZE(sortType.isAsc)
    }
}

private fun SearchCondition.Range.toEntity() = when (this) {
    SearchCondition.Range.BOOKSHELF -> SearchConditionEntity.Range.BOOKSHELF
    is SearchCondition.Range.SubFolder -> SearchConditionEntity.Range.FolderBelow(parent)
    is SearchCondition.Range.InFolder -> SearchConditionEntity.Range.InFolder(parent)
}

private fun SearchCondition.Period.toEntity() = when (this) {
    SearchCondition.Period.NONE -> SearchConditionEntity.Period.NONE
    SearchCondition.Period.HOUR_24 -> SearchConditionEntity.Period.HOUR_24
    SearchCondition.Period.WEEK_1 -> SearchConditionEntity.Period.WEEK_1
    SearchCondition.Period.MONTH_1 -> SearchConditionEntity.Period.MONTH_1
}

private fun SearchCondition.Order.toEntity() = when (this) {
    SearchCondition.Order.NAME -> SearchConditionEntity.Order.NAME
    SearchCondition.Order.DATE -> SearchConditionEntity.Order.DATE
    SearchCondition.Order.SIZE -> SearchConditionEntity.Order.SIZE
}

private fun SearchCondition.Sort.toEntity() = when (this) {
    SearchCondition.Sort.ASC -> SearchConditionEntity.Sort.ASC
    SearchCondition.Sort.DESC -> SearchConditionEntity.Sort.DESC
}

private fun SearchCondition.toEntity() = SearchConditionEntity(
    query,
    range.toEntity(),
    period.toEntity(),
    order.toEntity(),
    sort.toEntity()
)
