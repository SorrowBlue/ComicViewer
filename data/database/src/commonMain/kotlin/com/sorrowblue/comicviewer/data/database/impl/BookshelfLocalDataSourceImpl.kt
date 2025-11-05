package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@ContributesBinding(DataScope::class)
@Inject
internal class BookshelfLocalDataSourceImpl(
    private val dao: BookshelfDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BookshelfLocalDataSource {
    override suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean) {
        withContext(dispatcher) {
            dao.updateDeleted(bookshelfId.value, if (isDeleted) 1 else 0)
        }
    }

    override suspend fun updateOrCreate(bookshelf: Bookshelf, transaction: suspend (Bookshelf) -> Unit): Bookshelf? {
        val entity = BookshelfEntity.fromModel(bookshelf)
        return dao.database.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                dao
                    .upsert(entity)
                    .let {
                        if (it == -1L) {
                            dao.flow(bookshelf.id.value).first()
                        } else {
                            dao.flow(it.toInt()).first()
                        }
                    }?.toModel(0)
                    ?.also {
                        transaction(it)
                    }
            }
        }
    }

    override suspend fun delete(bookshelfId: BookshelfId): Resource<Unit, Resource.SystemError> = runCatching {
        withContext(dispatcher) {
            dao.delete(bookshelfId.value)
        }
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = { Resource.Error(Resource.SystemError(it)) },
    )

    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> = if (bookshelfId == ShareContents.id) {
        flowOf(ShareContents)
    } else {
        dao.flow(bookshelfId.value).map { it?.toModel(0) }.flowOn(dispatcher)
    }

    override fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>> = Pager(pagingConfig) {
        dao.pagingSourceNoDeleted()
    }.flow.map { pagingData ->
        pagingData.map {
            BookshelfFolder(it.entity.toModel(it.fileCount), it.fileEntity.toModel() as Folder)
        }
    }

    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> = kotlin
        .runCatching {
            dao.allBookshelf().map { list -> list.map { it.toModel(0) } }.flowOn(dispatcher)
        }.fold(
            onSuccess = {
                Resource.Success(it)
            },
            onFailure = {
                Resource.Error(Resource.SystemError(it))
            },
        )
}
