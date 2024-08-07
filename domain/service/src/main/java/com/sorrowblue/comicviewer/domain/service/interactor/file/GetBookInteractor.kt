package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.Unknown
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageFolderSettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class GetBookInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val folderSettingsInteractor: ManageFolderSettingsInteractor,
) : GetBookUseCase() {

    override fun run(request: Request): Flow<Result<Book, Unit>> {
        return kotlin.runCatching {
            bookshelfLocalDataSource.flow(request.bookshelfId).filterNotNull().map {
                val resolveImageFolder =
                    folderSettingsInteractor.settings.first().resolveImageFolder
                val remoteDataSource = remoteDataSourceFactory.create(it)
                val book = remoteDataSource.file(request.path, resolveImageFolder) as Book
                val reader = remoteDataSource.fileReader(book)
                val pageCount = reader?.pageCount() ?: 0
                val newBook = when (book) {
                    is BookFile -> book.copy(totalPageCount = pageCount)
                    is BookFolder -> book.copy(totalPageCount = pageCount)
                }
                fileLocalDataSource.addUpdate(newBook)
                newBook
            }
        }.fold({ fileModelFlow ->
            fileModelFlow.map {
                Result.Success(it)
            }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }
}
