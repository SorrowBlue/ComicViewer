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
import com.sorrowblue.comicviewer.data.toBookshelf
import com.sorrowblue.comicviewer.data.toBookshelfId
import com.sorrowblue.comicviewer.data.toBookshelfModel
import com.sorrowblue.comicviewer.data.toFileModel
import com.sorrowblue.comicviewer.data.toServerFolder
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.BookFolder
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.file.IFolder
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepositoryStatus
import com.sorrowblue.comicviewer.domain.repository.LibraryStatus
import com.sorrowblue.comicviewer.framework.Resource
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class BookshelfRepositoryImpl @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileModelLocalDataSource: FileModelLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory
) : BookshelfRepository {

    override fun connect(bookshelf: Bookshelf, path: String) =
        flow<Resource<Unit, BookshelfRepository.Error>> {
            remoteDataSourceFactory.create(bookshelf.toBookshelfModel()).connect(path)
            emit(Resource.Success(Unit))
        }.catch {
            emit(
                when (it as RemoteException) {
                    RemoteException.InvalidAuth -> Resource.Error(BookshelfRepository.Error.NotFound)
                    RemoteException.InvalidServer -> Resource.Error(BookshelfRepository.Error.NotFound)
                    RemoteException.NotFound -> Resource.Error(BookshelfRepository.Error.NotFound)
                    RemoteException.NoNetwork -> Resource.Error(BookshelfRepository.Error.Network)
                    RemoteException.Unknown -> Resource.Error(BookshelfRepository.Error.System)
                }
            )
        }.flowOn(Dispatchers.IO)

    override fun register(
        bookshelf: Bookshelf,
        folder: IFolder
    ): Flow<Resource<Bookshelf, BookshelfRepository.Error>> {
        return flow<Resource<Bookshelf, BookshelfRepository.Error>> {
            val bookshelfModel = bookshelfLocalDataSource.create(bookshelf.toBookshelfModel())
            val folderModel = when (folder) {
                is BookFolder -> folder.copy(
                    bookshelfId = bookshelfModel.id.toBookshelfId(),
                    parent = ""
                ).toFileModel()

                is Folder -> folder.copy(
                    bookshelfId = bookshelfModel.id.toBookshelfId(),
                    parent = ""
                ).toFileModel()
            }
            fileModelLocalDataSource.addUpdate(folderModel)
            emit(Resource.Success(bookshelfModel.toBookshelf()))
        }.catch {
            emit(Resource.Error(BookshelfRepository.Error.System))
        }.flowOn(Dispatchers.IO)
    }

    override fun find(bookshelfId: BookshelfId): Flow<Resource<Bookshelf, BookshelfRepository.Error>> {
        return bookshelfLocalDataSource.flow(BookshelfModelId(bookshelfId.value)).map {
            it?.toBookshelf()?.let {
                Resource.Success(it)
            } ?: Resource.Error(BookshelfRepository.Error.NotFound)
        }
    }

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
            remoteDataSourceFactory.create(bookshelf.toBookshelfModel()).exists(path)
        )
    }

    override suspend fun registerOrUpdate(
        bookshelf: Bookshelf,
        path: String
    ): Result<Bookshelf, BookshelfRepositoryStatus> {
        val serverModel = bookshelfLocalDataSource.create(bookshelf.toBookshelfModel())
        val fileModel = remoteDataSourceFactory.create(serverModel).fileModel(path)
        when (fileModel) {
            is FileModel.File -> fileModel.copy(parent = "")
            is FileModel.Folder -> fileModel.copy(parent = "")
            is FileModel.ImageFolder -> fileModel.copy(parent = "")
        }.let {
            fileModelLocalDataSource.addUpdate(it)
        }
        return Result.Success(serverModel.toBookshelf())
    }

    override suspend fun delete(bookshelf: Bookshelf): Response<Boolean> {
        bookshelfLocalDataSource.delete(bookshelf.toBookshelfModel())
        return Response.Success(true)
    }

    override fun get(bookshelfId: BookshelfId): Flow<Result<Bookshelf, LibraryStatus>> {
        return kotlin.runCatching {
            bookshelfLocalDataSource.flow(BookshelfModelId(bookshelfId.value))
                .flowOn(Dispatchers.IO)
        }.fold({ modelFlow ->
            modelFlow.map {
                if (it != null) {
                    Result.Success(it.toBookshelf())
                } else {
                    Result.Error(LibraryStatus.FAILED_CONNECT)
                }
            }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }

}
