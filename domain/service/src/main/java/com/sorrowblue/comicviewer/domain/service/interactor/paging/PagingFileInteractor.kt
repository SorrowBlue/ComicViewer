package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

internal class PagingFileInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : PagingFileUseCase() {
    override fun run(request: Request): Flow<Resource<Flow<PagingData<File>>, Error>> {
        return bookshelfLocalDataSource.flow(request.bookshelfId).map { bookshelf ->
            if (bookshelf != null) {
                val file = fileLocalDataSource.findBy(request.bookshelfId, request.path)
                if (file is IFolder) {
                    Resource.Success(
                        fileLocalDataSource.pagingSource(
                            request.pagingConfig,
                            bookshelf,
                            file
                        ) {
                            val settings =
                                runBlocking { datastoreDataSource.folderDisplaySettings.first() }
                            SearchCondition(
                                "",
                                SearchCondition.Range.InFolder(file.path),
                                SearchCondition.Period.NONE,
                                when (settings.sortType) {
                                    is SortType.Date -> SearchCondition.Order.DATE
                                    is SortType.Name -> SearchCondition.Order.NAME
                                    is SortType.Size -> SearchCondition.Order.SIZE
                                },
                                if (settings.sortType.isAsc) SearchCondition.Sort.ASC else SearchCondition.Sort.DESC,
                                settings.showHiddenFiles,
                            )
                        }
                    )
                } else {
                    Resource.Error(Error.NOT_FOUND)
                }
            } else {
                Resource.Error(Error.NOT_FOUND)
            }
        }
    }
}
