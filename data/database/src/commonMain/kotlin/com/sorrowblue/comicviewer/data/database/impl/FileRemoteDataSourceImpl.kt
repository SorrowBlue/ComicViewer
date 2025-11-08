package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.pagingSourceFileSearch
import com.sorrowblue.comicviewer.data.database.entity.file.QueryFileWithCountEntity
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.FileRemoteDataSource
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
internal class FileRemoteDataSourceImpl(
    private val dao: FileDao,
    private val factory: FileModelRemoteMediator.Factory,
) : FileRemoteDataSource {
    @OptIn(ExperimentalPagingApi::class)
    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> {
        val remoteMediator = factory.create(bookshelf, file)
        return Pager(pagingConfig, remoteMediator = remoteMediator) {
            dao.pagingSourceFileSearch(bookshelf.id, searchCondition())
        }.flow.map { it.map(QueryFileWithCountEntity::toModel) }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingSourceBookThumbnail(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<BookThumbnail>> {
        val remoteMediator = factory.create(bookshelf, file)
        return Pager(pagingConfig, remoteMediator = remoteMediator) {
            dao.pagingSourceFileSearch(bookshelf.id, searchCondition())
        }.flow.map { pagingData ->
            pagingData.map {
                BookThumbnail.Companion.from(it.toModel() as Book)
            }
        }
    }
}
