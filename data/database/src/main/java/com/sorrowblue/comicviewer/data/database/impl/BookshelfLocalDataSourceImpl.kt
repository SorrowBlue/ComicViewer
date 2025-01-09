package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class BookshelfLocalDataSourceImpl @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val dao: BookshelfDao,
) : BookshelfLocalDataSource {

    override suspend fun updateDeleted(bookshelfId: BookshelfId, isDeleted: Boolean) {
        withContext(dispatcher) {
            dao.updateDeleted(bookshelfId.value, if (isDeleted) 1 else 0)
        }
    }

    override suspend fun updateOrCreate(bookshelf: Bookshelf): Bookshelf? {
        val entity = BookshelfEntity.fromModel(bookshelf)
        return dao.upsert(entity).let {
            if (it == -1L) {
                dao.flow(bookshelf.id.value).first()
            } else {
                dao.flow(it.toInt()).first()
            }
        }?.toModel(0)
    }

    override suspend fun delete(bookshelfId: BookshelfId): Resource<Unit, Resource.SystemError> {
        return runCatching {
            withContext(dispatcher) {
                dao.delete(bookshelfId)
            }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(Resource.SystemError(it)) }
        )
    }

    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> {
        return if (bookshelfId == ShareContents.id) {
            flowOf(ShareContents)
        } else {
            dao.flow(bookshelfId.value).map { it?.toModel(0) }.flowOn(dispatcher)
        }
    }

    override fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>> {
        return Pager(pagingConfig) { dao.pagingSource() }.flow.map { pagingData ->
            pagingData.map {
                BookshelfFolder(it.entity.toModel(it.fileCount), it.fileEntity.toModel() as Folder)
            }
        }
    }

    override fun pagingSource(
        bookshelfId: BookshelfId,
        pagingConfig: PagingConfig,
    ): Flow<PagingData<BookThumbnail>> {
        return Pager(pagingConfig) { dao.pagingSource(bookshelfId) }.flow.map { pagingData ->
            pagingData.map { BookThumbnail.from(it.toModel() as Book) }
        }
    }

    override fun allBookshelf(): Resource<Flow<List<Bookshelf>>, Resource.SystemError> {
        return kotlin.runCatching {
            dao.allBookshelf().map { list -> list.map { it.toModel(0) } }.flowOn(dispatcher)
        }.fold(
            onSuccess = {
                Resource.Success(it)
            },
            onFailure = {
                Resource.Error(Resource.SystemError(it))
            }
        )
    }
}
