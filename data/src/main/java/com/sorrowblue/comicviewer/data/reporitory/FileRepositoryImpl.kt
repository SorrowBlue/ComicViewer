package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ScanTypeModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SortType
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.toFile
import com.sorrowblue.comicviewer.data.toFileModel
import com.sorrowblue.comicviewer.data.toServerModel
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryError
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class FileRepositoryImpl @Inject constructor(
    private val fileScanService: FileScanService,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val settingsCommonRepository: SettingsCommonRepository
) : FileRepository {

    override suspend fun getBook(serverId: ServerId, path: String): Response<Book?> {
        return Response.Success(
            fileModelLocalDataSource.findBy(ServerModelId(serverId.value), path)?.toFile() as? Book
        )
    }

    override fun getFile(serverId: ServerId, path: String): Flow<Result<File, Unit>> {
        return kotlin.runCatching {
            fileModelLocalDataSource.selectBy(ServerModelId(serverId.value), path)
        }.fold({ fileModelFlow ->
            fileModelFlow.map {
                if (it != null) Result.Success(it.toFile()) else Result.Error(Unit)
            }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }

    override suspend fun update(
        serverId: ServerId,
        path: String,
        lastReadPage: Int,
        lastReadTime: Long
    ) {
        fileModelLocalDataSource.update(
            path,
            ServerModelId(serverId.value),
            lastReadPage,
            lastReadTime
        )
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        server: Server,
        bookshelf: Bookshelf
    ): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingSource(
            pagingConfig,
            server.toServerModel(),
            bookshelf.toFileModel()
        ) {
            val settings = runBlocking { settingsCommonRepository.bookshelfDisplaySettings.first() }
            when (settings.sort) {
                BookshelfDisplaySettings.Sort.NAME -> SortType.NAME(settings.order == BookshelfDisplaySettings.Order.ASC)
                BookshelfDisplaySettings.Sort.DATE -> SortType.DATE(settings.order == BookshelfDisplaySettings.Order.ASC)
                BookshelfDisplaySettings.Sort.SIZE -> SortType.SIZE(settings.order == BookshelfDisplaySettings.Order.ASC)
            }
        }.map { it.map(FileModel::toFile) }
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        server: Server,
        query: () -> String,
    ): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingSource(
            pagingConfig,
            ServerModelId(server.id.value),
            query
        ) {
            val settings = runBlocking { settingsCommonRepository.bookshelfDisplaySettings.first() }
            when (settings.sort) {
                BookshelfDisplaySettings.Sort.NAME -> SortType.NAME(settings.order == BookshelfDisplaySettings.Order.ASC)
                BookshelfDisplaySettings.Sort.DATE -> SortType.DATE(settings.order == BookshelfDisplaySettings.Order.ASC)
                BookshelfDisplaySettings.Sort.SIZE -> SortType.SIZE(settings.order == BookshelfDisplaySettings.Order.ASC)
            }
        }.map { pagingData -> pagingData.map(FileModel::toFile) }
    }

    override suspend fun get(serverId: ServerId, path: String): Response<File?> {
        return Response.Success(
            fileModelLocalDataSource.findBy(ServerModelId(serverId.value), path)?.toFile()
        )
    }

    override suspend fun list(serverId: ServerId): List<File> {
        return fileModelLocalDataSource.findBy(ServerModelId(serverId.value)).map { it.toFile() }
    }

    override suspend fun scan(bookshelf: Bookshelf, scanType: ScanType): String {
        val bookshelfSettings = settingsCommonRepository.bookshelfSettings.first()
        return fileScanService.enqueue(
            bookshelf.toFileModel(),
            when (scanType) {
                ScanType.FULL -> ScanTypeModel.FULL
                ScanType.QUICK -> ScanTypeModel.QUICK
            },
            bookshelfSettings.resolveImageFolder,
            bookshelfSettings.supportExtension.map(SupportExtension::extension)
        )
    }

    override suspend fun get2(serverId: ServerId, path: String): Result<File?, Unit> {
        return kotlin.runCatching {
            fileModelLocalDataSource.findBy(ServerModelId(serverId.value), path)
        }.fold({
            Result.Success(it?.toFile())
        }, {
            Result.Exception(Unknown(it))
        })
    }

    override suspend fun getRoot(serverId: ServerId): Result<File?, Unit> {
        return kotlin.runCatching {
            fileModelLocalDataSource.root(ServerModelId(serverId.value))
        }.fold({
            Result.Success(it?.toFile())
        }, {
            Result.Exception(Unknown(it))
        })
    }

    override suspend fun getFolder(
        server: Server,
        path: String
    ): Result<Bookshelf, FileRepositoryError> {
        return withContext(Dispatchers.IO) {
            val file =
                remoteDataSourceFactory.create(server.toServerModel()).fileModel(path).toFile()
            withContext(Dispatchers.IO) {
                if (file is Bookshelf) {
                    Result.Success(file)
                } else {
                    Result.Error(FileRepositoryError.PathDoesNotExist)
                }
            }
        }
    }

    override fun getNextRelFile(
        serverId: ServerId,
        path: String,
        isNext: Boolean
    ): Flow<Result<File, Unit>> {
        return kotlin.runCatching {
            if (isNext) {
                fileModelLocalDataSource.nextFileModel(ServerModelId(serverId.value), path)
            } else {
                fileModelLocalDataSource.prevFileModel(ServerModelId(serverId.value), path)
            }
        }.fold({
            it.map { if (it != null) Result.Success(it.toFile()) else Result.Error(Unit) }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }
}
