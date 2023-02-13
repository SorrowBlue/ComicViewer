package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.BookshelfFolderModel
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.entity.Bookshelf
import com.sorrowblue.comicviewer.data.datasource.BookshelfLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import logcat.logcat

internal class BookshelfLocalDataSourceImpl @Inject constructor(
    private val dao: BookshelfDao
) : BookshelfLocalDataSource {

    override suspend fun create(bookshelfModel: BookshelfModel): BookshelfModel {
        val entity = Bookshelf.fromModel(bookshelfModel)
        return dao.upsert(Bookshelf.fromModel(bookshelfModel)).let {
            logcat { "dao.upsert(): before=${entity.id}, after=$it" }
            if (it == -1L) {
                entity
            } else {
                entity.copy(id = it.toInt())
            }
        }.toModel(0)
    }

    override suspend fun delete(bookshelfModel: BookshelfModel): Int {
        return dao.delete(Bookshelf.fromModel(bookshelfModel))
    }

    override fun get(bookshelfModelId: BookshelfModelId): Flow<BookshelfModel?> {
        return dao.selectById(bookshelfModelId.value).map { it?.toModel(0) }
    }

    override fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<BookshelfFolderModel>> {
        return Pager(pagingConfig) { dao.pagingSource() }.flow.map { pagingData ->
            pagingData.map {
                BookshelfFolderModel(it.bookshelf.toModel(it.fileCount) to it.file.toModel() as FileModel.Folder)
            }
        }

    }
}

