package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileRemoteDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking

@Inject
internal class PagingFileInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val fileRemoteDataSource: FileRemoteDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : PagingFileUseCase() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(request: Request): Flow<PagingData<File>> {
        return bookshelfLocalDataSource.flow(request.bookshelfId).flatMapLatest { bookshelf ->
            val file = fileLocalDataSource.findBy(request.bookshelfId, request.path) as IFolder
            fileRemoteDataSource.pagingDataFlow(request.pagingConfig, bookshelf!!, file) {
                val settings =
                    runBlocking { datastoreDataSource.folderDisplaySettings.first() }
                SearchCondition(
                    "",
                    SearchCondition.Range.InFolder(file.path),
                    SearchCondition.Period.None,
                    settings.currentSortType(file.bookshelfId, file.path),
                    settings.showHiddenFiles,
                )
            }
        }
    }
}
