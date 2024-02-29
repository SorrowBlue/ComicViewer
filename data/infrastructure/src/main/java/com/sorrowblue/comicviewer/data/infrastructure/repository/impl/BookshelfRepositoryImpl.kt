package com.sorrowblue.comicviewer.data.infrastructure.repository.impl

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.infrastructure.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.data.infrastructure.di.IoDispatcher
import com.sorrowblue.comicviewer.data.infrastructure.exception.RemoteException
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.Unknown
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepositoryStatus
import com.sorrowblue.comicviewer.domain.service.repository.LibraryStatus
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class BookshelfRepositoryImpl @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
) : BookshelfRepository {

    override fun connect(bookshelf: Bookshelf, path: String) =
        flow<Resource<Unit, BookshelfRepository.Error>> {
            remoteDataSourceFactory.create(bookshelf).connect(path)
            emit(Resource.Success(Unit))
        }.catch {
            emit(
                when (it as RemoteException) {
                    RemoteException.InvalidAuth -> Resource.Error(BookshelfRepository.Error.InvalidAuth)
                    RemoteException.InvalidServer -> Resource.Error(BookshelfRepository.Error.InvalidServer)
                    RemoteException.NotFound -> Resource.Error(BookshelfRepository.Error.NotFound)
                    RemoteException.NoNetwork -> Resource.Error(BookshelfRepository.Error.Network)
                    RemoteException.Unknown -> Resource.Error(BookshelfRepository.Error.System)
                }
            )
        }.flowOn(dispatcher)

    override fun getAttribute(bookshelf: Bookshelf, path: String) =
        flow<Resource<FileAttribute?, BookshelfRepository.Error>> {
            emit(Resource.Success(remoteDataSourceFactory.create(bookshelf).getAttribute(path)))
        }.catch {
            emit(
                when (it as RemoteException) {
                    RemoteException.InvalidAuth -> Resource.Error(BookshelfRepository.Error.InvalidAuth)
                    RemoteException.InvalidServer -> Resource.Error(BookshelfRepository.Error.InvalidServer)
                    RemoteException.NotFound -> Resource.Error(BookshelfRepository.Error.NotFound)
                    RemoteException.NoNetwork -> Resource.Error(BookshelfRepository.Error.Network)
                    RemoteException.Unknown -> Resource.Error(BookshelfRepository.Error.System)
                }
            )
        }.flowOn(dispatcher)

    override fun register(
        bookshelf: Bookshelf,
        folder: IFolder,
    ): Flow<Resource<Bookshelf, BookshelfRepository.Error>> {
        return flow<Resource<Bookshelf, BookshelfRepository.Error>> {
            val model = bookshelfLocalDataSource.create(bookshelf)
            val folderModel = when (folder) {
                is BookFolder -> folder.copy(bookshelfId = model.id, parent = "")
                is Folder -> folder.copy(bookshelfId = model.id, parent = "")
            }
            fileModelLocalDataSource.addUpdate(folderModel)
            emit(Resource.Success(model))
        }.catch {
            emit(Resource.Error(BookshelfRepository.Error.System))
        }.flowOn(dispatcher)
    }

    override fun find(bookshelfId: BookshelfId): Flow<Resource<Bookshelf, BookshelfRepository.Error>> {
        return bookshelfLocalDataSource.flow(bookshelfId).map { bookshelf ->
            bookshelf?.let { Resource.Success(it) }
                ?: Resource.Error(BookshelfRepository.Error.NotFound)
        }
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>> {
        return bookshelfLocalDataSource.pagingSource(pagingConfig)
    }

    override suspend fun exists(
        bookshelf: Bookshelf,
        path: String,
    ): Result<Boolean, BookshelfRepositoryStatus> {
        return Result.Success(
            remoteDataSourceFactory.create(bookshelf).exists(path)
        )
    }

    override suspend fun registerOrUpdate(
        bookshelf: Bookshelf,
        path: String,
    ): Result<Bookshelf, BookshelfRepositoryStatus> {
        val serverModel = bookshelfLocalDataSource.create(bookshelf)
        val fileModel = remoteDataSourceFactory.create(serverModel).file(path)
        when (fileModel) {
            is BookFile -> fileModel.copy(parent = "")
            is Folder -> fileModel.copy(parent = "")
            is BookFolder -> fileModel.copy(parent = "")
        }.let {
            fileModelLocalDataSource.addUpdate(it)
        }
        return Result.Success(serverModel)
    }

    override suspend fun delete(bookshelf: Bookshelf): Response<Boolean> {
        bookshelfLocalDataSource.delete(bookshelf)
        return Response.Success(true)
    }

    override fun get(bookshelfId: BookshelfId): Flow<Result<Bookshelf, LibraryStatus>> {
        return kotlin.runCatching {
            bookshelfLocalDataSource.flow(bookshelfId)
                .flowOn(dispatcher)
        }.fold({ modelFlow ->
            modelFlow.map {
                if (it != null) {
                    Result.Success(it)
                } else {
                    Result.Error(LibraryStatus.FAILED_CONNECT)
                }
            }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }
}
