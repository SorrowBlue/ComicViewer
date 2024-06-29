package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.entity.BookshelfEntity
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class BookshelfLocalDataSourceImpl @Inject constructor(
    private val dao: BookshelfDao,
) : BookshelfLocalDataSource {

    override suspend fun create(bookshelf: Bookshelf): Bookshelf {
        val entity = BookshelfEntity.fromModel(bookshelf)
        return dao.upsert(entity).let {
            if (it == -1L) {
                entity
            } else {
                entity.copy(id = BookshelfId(it.toInt()))
            }
        }.toModel(0)
    }

    override suspend fun delete(bookshelf: Bookshelf): Int {
        return dao.delete(BookshelfEntity.fromModel(bookshelf))
    }

    override fun flow(bookshelfId: BookshelfId): Flow<Bookshelf?> {
        return if (bookshelfId == ShareContents.id) {
            flowOf(ShareContents)
        } else {
            dao.flow(bookshelfId.value).map { it?.toModel(0) }
        }
    }

    override fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolder>> {
        return Pager(pagingConfig) { dao.pagingSource() }.flow.map { pagingData ->
            pagingData.map {
                BookshelfFolder(it.entity.toModel(it.fileCount), it.fileEntity.toModel() as Folder)
            }
        }
    }
}
