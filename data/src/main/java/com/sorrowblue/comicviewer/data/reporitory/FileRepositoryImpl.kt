package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ReadLaterFileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.ScanTypeModel
import com.sorrowblue.comicviewer.data.common.bookshelf.SearchConditionEntity2
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.data.datasource.ReadLaterFileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.toBookshelfModel
import com.sorrowblue.comicviewer.data.toFile
import com.sorrowblue.comicviewer.data.toFileModel
import com.sorrowblue.comicviewer.domain.entity.SearchCondition
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.file.IFolder
import com.sorrowblue.comicviewer.domain.entity.settings.SortType
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryError
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
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
            readLaterFileModelLocalDataSource.add(ReadLaterFileModel(BookshelfModelId(bookshelfId.value), path))
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
                    fileModelLocalDataSource.root(BookshelfModelId(bookshelfId.value))!!.toFile()
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
            BookshelfModelId(bookshelf.id.value)
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
                        BookshelfModelId(bookshelfId.value),
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
        fileModelLocalDataSource.deleteHistory(BookshelfModelId(bookshelfId.value), list)
    }

    override suspend fun getBook(bookshelfId: BookshelfId, path: String): Response<Book?> {
        return Response.Success(
            fileModelLocalDataSource.findBy(BookshelfModelId(bookshelfId.value), path)
                ?.toFile() as? Book
        )
    }

    override fun getFile(bookshelfId: BookshelfId, path: String): Flow<Result<File, Unit>> {
        return kotlin.runCatching {
            fileModelLocalDataSource.flow(BookshelfModelId(bookshelfId.value), path)
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
            BookshelfModelId(bookshelfId.value),
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
            bookshelf.toBookshelfModel(),
            folder.toFileModel()
        ) {
            val settings = runBlocking { settingsCommonRepository.folderDisplaySettings.first() }
            SortEntity.from(settings.sortType)
        }.map { it.map(FileModel::toFile) }
    }

    override suspend fun get(bookshelfId: BookshelfId, path: String): Response<File?> {
        return Response.Success(
            fileModelLocalDataSource.findBy(BookshelfModelId(bookshelfId.value), path)?.toFile()
        )
    }

    override suspend fun scan(folder: IFolder, scanType: ScanType): String {
        val folderSettings = settingsCommonRepository.folderSettings.first()
        return fileScanService.enqueue(
            folder.toFileModel(),
            when (scanType) {
                ScanType.FULL -> ScanTypeModel.FULL
                ScanType.QUICK -> ScanTypeModel.QUICK
            },
            folderSettings.resolveImageFolder,
            folderSettings.supportExtension.map(SupportExtension::extension)
        )
    }

    override suspend fun get2(bookshelfId: BookshelfId, path: String): Result<File?, Unit> {
        return kotlin.runCatching {
            fileModelLocalDataSource.findBy(BookshelfModelId(bookshelfId.value), path)
        }.fold({
            Result.Success(it?.toFile())
        }, {
            Result.Exception(Unknown(it))
        })
    }

    override suspend fun getRoot(bookshelfId: BookshelfId): Result<File?, Unit> {
        return kotlin.runCatching {
            fileModelLocalDataSource.root(BookshelfModelId(bookshelfId.value))
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
                fileModelLocalDataSource.nextFileModel(BookshelfModelId(bookshelfId.value), path, sortEntity)
            } else {
                fileModelLocalDataSource.prevFileModel(BookshelfModelId(bookshelfId.value), path, sortEntity)
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
    SearchCondition.Range.BOOKSHELF -> SearchConditionEntity2.Range.BOOKSHELF
    is SearchCondition.Range.FolderBelow -> SearchConditionEntity2.Range.FolderBelow(parent)
    is SearchCondition.Range.InFolder -> SearchConditionEntity2.Range.InFolder(parent)
}

private fun SearchCondition.Period.toEntity() = when (this) {
    SearchCondition.Period.NONE -> SearchConditionEntity2.Period.NONE
    SearchCondition.Period.HOUR_24 -> SearchConditionEntity2.Period.HOUR_24
    SearchCondition.Period.WEEK_1 -> SearchConditionEntity2.Period.WEEK_1
    SearchCondition.Period.MONTH_1 -> SearchConditionEntity2.Period.MONTH_1
}

private fun SearchCondition.Order.toEntity() = when (this) {
    SearchCondition.Order.NAME -> SearchConditionEntity2.Order.NAME
    SearchCondition.Order.DATE -> SearchConditionEntity2.Order.DATE
    SearchCondition.Order.SIZE -> SearchConditionEntity2.Order.SIZE
}

private fun SearchCondition.Sort.toEntity() = when (this) {
    SearchCondition.Sort.ASC -> SearchConditionEntity2.Sort.ASC
    SearchCondition.Sort.DESC -> SearchConditionEntity2.Sort.DESC
}

private fun SearchCondition.toEntity() = SearchConditionEntity2(
    query,
    range.toEntity(),
    period.toEntity(),
    order.toEntity(),
    sort.toEntity()
)
