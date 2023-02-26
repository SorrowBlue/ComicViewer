package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.ScanTypeModel
import com.sorrowblue.comicviewer.data.common.bookshelf.SortType
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.toFile
import com.sorrowblue.comicviewer.data.toFileModel
import com.sorrowblue.comicviewer.data.toServerModel
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
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

    override suspend fun getBook(bookshelfId: BookshelfId, path: String): Response<Book?> {
        return Response.Success(
            fileModelLocalDataSource.findBy(BookshelfModelId(bookshelfId.value), path)?.toFile() as? Book
        )
    }

    override fun getFile(bookshelfId: BookshelfId, path: String): Flow<Result<File, Unit>> {
        return kotlin.runCatching {
            fileModelLocalDataSource.selectBy(BookshelfModelId(bookshelfId.value), path)
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
        fileModelLocalDataSource.update(
            path,
            BookshelfModelId(bookshelfId.value),
            lastReadPage,
            lastReadTime
        )
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        folder: Folder
    ): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingSource(
            pagingConfig,
            bookshelf.toServerModel(),
            folder.toFileModel()
        ) {
            val settings = runBlocking { settingsCommonRepository.folderDisplaySettings.first() }
            when (settings.sort) {
                FolderDisplaySettings.Sort.NAME -> SortType.NAME(settings.order == FolderDisplaySettings.Order.ASC)
                FolderDisplaySettings.Sort.DATE -> SortType.DATE(settings.order == FolderDisplaySettings.Order.ASC)
                FolderDisplaySettings.Sort.SIZE -> SortType.SIZE(settings.order == FolderDisplaySettings.Order.ASC)
            }
        }.map { it.map(FileModel::toFile) }
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        query: () -> String,
    ): Flow<PagingData<File>> {
        return fileModelLocalDataSource.pagingSource(
            pagingConfig,
            BookshelfModelId(bookshelf.id.value),
            query
        ) {
            val settings = runBlocking { settingsCommonRepository.folderDisplaySettings.first() }
            when (settings.sort) {
                FolderDisplaySettings.Sort.NAME -> SortType.NAME(settings.order == FolderDisplaySettings.Order.ASC)
                FolderDisplaySettings.Sort.DATE -> SortType.DATE(settings.order == FolderDisplaySettings.Order.ASC)
                FolderDisplaySettings.Sort.SIZE -> SortType.SIZE(settings.order == FolderDisplaySettings.Order.ASC)
            }
        }.map { pagingData -> pagingData.map(FileModel::toFile) }
    }

    override suspend fun get(bookshelfId: BookshelfId, path: String): Response<File?> {
        return Response.Success(
            fileModelLocalDataSource.findBy(BookshelfModelId(bookshelfId.value), path)?.toFile()
        )
    }

    override suspend fun list(bookshelfId: BookshelfId): List<File> {
        return fileModelLocalDataSource.findBy(BookshelfModelId(bookshelfId.value)).map { it.toFile() }
    }

    override suspend fun scan(folder: Folder, scanType: ScanType): String {
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
            val file =
                remoteDataSourceFactory.create(bookshelf.toServerModel()).fileModel(path).toFile()
            withContext(Dispatchers.IO) {
                if (file is Folder) {
                    Result.Success(file)
                } else {
                    Result.Error(FileRepositoryError.PathDoesNotExist)
                }
            }
        }
    }

    override fun getNextRelFile(
        bookshelfId: BookshelfId,
        path: String,
        isNext: Boolean
    ): Flow<Result<File, Unit>> {
        return kotlin.runCatching {
            if (isNext) {
                fileModelLocalDataSource.nextFileModel(BookshelfModelId(bookshelfId.value), path)
            } else {
                fileModelLocalDataSource.prevFileModel(BookshelfModelId(bookshelfId.value), path)
            }
        }.fold({ modelFlow ->
            modelFlow.map { if (it != null) Result.Success(it.toFile()) else Result.Error(Unit) }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }
}
