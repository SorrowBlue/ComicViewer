package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Factory

@Factory
internal class PagingFileInteractor(
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
                        fileLocalDataSource.pagingDataFlow(
                            request.pagingConfig,
                            bookshelf,
                            file
                        ) {
                            val settings =
                                runBlocking { datastoreDataSource.folderDisplaySettings.first() }
                            SearchCondition(
                                "",
                                SearchCondition.Range.InFolder(file.path),
                                SearchCondition.Period.None,
                                settings.folderScopeOnlyList.find { it.path == file.path && it.bookshelfId == file.bookshelfId }?.sortType
                                    ?: settings.sortType,
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
