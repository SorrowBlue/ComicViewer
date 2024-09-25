package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFolderBookThumbnailsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking

internal class PagingFolderBookThumbnailsUseCaseInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : PagingFolderBookThumbnailsUseCase() {

    override fun run(request: Request): Flow<PagingData<BookThumbnail>> {
        return bookshelfLocalDataSource.flow(request.bookshelfId).flatMapLatest { bookshelf ->
            if (bookshelf != null) {
                val file = fileLocalDataSource.findBy(request.bookshelfId, request.path)
                if (file is IFolder) {
                    fileLocalDataSource.pagingSourceBookThumbnail(
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
                            settings.sortType,
                            settings.showHiddenFiles,
                        )
                    }
                } else {
                    throw RuntimeException("Not found")
                }
            } else {
                throw RuntimeException("Not found")
            }
        }
    }
}
