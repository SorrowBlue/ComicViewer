package com.sorrowblue.comicviewer.data.reporitory.impl

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.BookshelfFolderModel
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.exception.RemoteException
import com.sorrowblue.comicviewer.data.toFileModel
import com.sorrowblue.comicviewer.data.toServer
import com.sorrowblue.comicviewer.data.toServerFolder
import com.sorrowblue.comicviewer.data.toServerId
import com.sorrowblue.comicviewer.data.toServerModel
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepositoryError
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepositoryStatus
import com.sorrowblue.comicviewer.domain.repository.LibraryStatus
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryError
import com.sorrowblue.comicviewer.framework.NoNetwork
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import logcat.logcat

internal class BookshelfRepositoryImpl @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory
) : BookshelfRepository {

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>> {
        return bookshelfLocalDataSource.pagingSource(pagingConfig).map {
            it.map(BookshelfFolderModel::toServerFolder)
        }
    }

    override suspend fun exists(
        bookshelf: Bookshelf,
        path: String
    ): Result<Boolean, BookshelfRepositoryStatus> {
        return Result.Success(
            remoteDataSourceFactory.create(bookshelf.toServerModel()).exists(path)
        )
    }

    override suspend fun registerOrUpdate(
        bookshelf: Bookshelf,
        path: String
    ): Result<Bookshelf, RegisterLibraryError> {
        val serverModel = bookshelfLocalDataSource.create(bookshelf.toServerModel())
        val fileModel = remoteDataSourceFactory.create(serverModel).fileModel(path)
        when (fileModel) {
            is FileModel.File -> fileModel.copy(parent = "")
            is FileModel.Folder -> fileModel.copy(parent = "")
            is FileModel.ImageFolder -> fileModel.copy(parent = "")
        }.let {
            fileModelLocalDataSource.register(it)
        }
        return Result.Success(serverModel.toServer())
    }

    override suspend fun delete(bookshelf: Bookshelf): Response<Boolean> {
        bookshelfLocalDataSource.delete(bookshelf.toServerModel())
        return Response.Success(true)
    }

    override fun get(bookshelfId: BookshelfId): Flow<Result<Bookshelf, LibraryStatus>> {
        return kotlin.runCatching {
            bookshelfLocalDataSource.get(BookshelfModelId(bookshelfId.value)).flowOn(Dispatchers.IO)
        }.fold({
            it.map {
                if (it != null) {
                    Result.Success(it.toServer())
                } else {
                    Result.Error(LibraryStatus.FAILED_CONNECT)
                }
            }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }

    override suspend fun connect(bookshelf: Bookshelf, path: String): Result<Unit, BookshelfRepositoryError> {
        return withContext(Dispatchers.IO) {
            val remoteDataSource = remoteDataSourceFactory.create(bookshelf.toServerModel())
            kotlin.runCatching {
                remoteDataSource.connect(path)
                Result.Success(Unit)
            }.getOrElse {
                when (it as? RemoteException) {
                    RemoteException.InvalidAuth -> Result.Error(BookshelfRepositoryError.AuthenticationFailure)
                    RemoteException.InvalidServer -> Result.Error(BookshelfRepositoryError.IncorrectServerInfo)
                    RemoteException.NoNetwork -> Result.Exception(NoNetwork)
                    RemoteException.NotFound -> Result.Error(BookshelfRepositoryError.PathDoesNotExist)
                    null -> Result.Exception(Unknown(it))
                }
            }
        }
    }

    override suspend fun register(
        bookshelf: Bookshelf,
        folder: Folder
    ): Result<Bookshelf, BookshelfRepositoryError> {
        return withContext(Dispatchers.IO) {
            val r = bookshelfLocalDataSource.create(bookshelf.toServerModel())
            val model = folder.copy(bookshelfId = r.id.toServerId(), parent = "").toFileModel()
            logcat { "model=${model}" }
            fileModelLocalDataSource.register(model)
            Result.Success(r.toServer())
        }
    }
}
