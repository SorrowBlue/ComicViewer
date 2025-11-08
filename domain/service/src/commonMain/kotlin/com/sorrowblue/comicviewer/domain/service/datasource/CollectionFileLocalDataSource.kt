package com.sorrowblue.comicviewer.domain.service.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.coroutines.flow.Flow

interface CollectionFileLocalDataSource {
    fun pagingDataFlow(
        id: CollectionId,
        pagingConfig: PagingConfig,
        sortType: () -> SortType,
    ): Flow<PagingData<File>>

    suspend fun add(file: CollectionFile)

    suspend fun remove(file: CollectionFile)

    fun flowNextCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?>

    fun flowPrevCollectionFile(file: CollectionFile, sortType: SortType): Flow<File?>

    suspend fun getCacheKeyList(id: CollectionId, limit: Int): List<Pair<BookshelfId, String>>
}
