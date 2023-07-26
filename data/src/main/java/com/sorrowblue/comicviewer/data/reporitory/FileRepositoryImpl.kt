package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.ScanTypeModel
import com.sorrowblue.comicviewer.data.common.bookshelf.SearchConditionEntity
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.ImageCacheDataSource
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class FileRepositoryImpl @Inject constructor(
    private val imageCacheDataSource: ImageCacheDataSource,
    private val fileScanService: FileScanService,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val settingsCommonRepository: SettingsCommonRepository
) : FileRepository {

    override fun findByParent(
        bookshelfId: BookshelfId,
        parent: String
    ): Flow<Resource<File, FileRepository.Error>> {
        return flow {
            emit(Resource.Success(fileModelLocalDataSource.root(BookshelfModelId(bookshelfId.value))!!.toFile()))
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

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        searchCondition: SearchCondition,
        sortType: () -> SortType
    ): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingSource(
            pagingConfig,
            BookshelfModelId(bookshelf.id.value),
            SearchConditionEntity.from(searchCondition)
        ) { SortEntity.from(sortType()) }
            .map { pagingData -> pagingData.map(FileModel::toFile) }
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

private fun SearchConditionEntity.Companion.from(searchCondition: SearchCondition): SearchConditionEntity {
    return SearchConditionEntity(
        searchCondition.query,
        when (val searchRange = searchCondition.range) {
            SearchCondition.Range.BOOKSHELF -> SearchConditionEntity.Range.BOOKSHELF
            is SearchCondition.Range.FOLDER_BELOW -> SearchConditionEntity.Range.FOLDER_BELOW(
                searchRange.parent
            )

            is SearchCondition.Range.IN_FOLDER -> SearchConditionEntity.Range.IN_FOLDER(searchRange.parent)
        },
        when (searchCondition.period) {
            SearchCondition.Period.NONE -> SearchConditionEntity.Period.NONE
            SearchCondition.Period.HOUR_24 -> SearchConditionEntity.Period.HOUR_24
            SearchCondition.Period.WEEK_1 -> SearchConditionEntity.Period.WEEK_1
            SearchCondition.Period.MONTH_1 -> SearchConditionEntity.Period.MONTH_1
        }
    )
}
