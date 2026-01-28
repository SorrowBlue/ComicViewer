package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.LocalDataSourceQueryError
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageFolderSettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import logcat.asLog
import logcat.logcat

@Inject
internal class GetBookInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val folderSettingsInteractor: ManageFolderSettingsInteractor,
) : GetBookUseCase() {
    override fun run(request: Request): Flow<Resource<Book, Error>> =
        bookshelfLocalDataSource.flow(request.bookshelfId).map {
            if (it != null) {
                fetch(it, request.path)
            } else {
                Resource.Error(Error.NotFound)
            }
        }

    private suspend fun fetch(bookshelf: Bookshelf, path: String): Resource<Book, Error> {
        logcat { "fetch(): bookshelf=$bookshelf, path=$path" }
        val remoteDataSource = remoteDataSourceFactory.create(bookshelf)
        val resolveImageFolder =
            folderSettingsInteractor.settings.first().resolveImageFolder
        val localFile = when (val file = remoteDataSource.file(path, resolveImageFolder)) {
            is BookFile -> fileLocalDataSource.updateSimple(file)
            is BookFolder -> fileLocalDataSource.updateSimple(file)
            is Folder -> return Resource.Error(Error.NotFound)
        }
        return localFile.fold(
            onSuccess = {
                when (it) {
                    is BookFile -> updateTotalPageCount(remoteDataSource, it)
                    is BookFolder -> updateTotalPageCount(remoteDataSource, it)
                    is Folder -> return Resource.Error(Error.NotFound)
                }
            },
            onError = {
                when (it) {
                    LocalDataSourceQueryError.NotFound -> Resource.Error(Error.NotFound)

                    is LocalDataSourceQueryError.SystemError -> {
                        // TODO Report Error
                        Resource.Error(Error.ReportedSystemError)
                    }
                }
            },
        )
    }

    private suspend fun updateTotalPageCount(
        datSource: RemoteDataSource,
        book: Book,
    ): Resource<Book, Error> {
        logcat { "updateTotalPageCount()" }
        return kotlin
            .runCatching {
                datSource
                    .fileReader(book)
                    ?.use {
                        it.pageCount()
                    }?.let { totalPageCount ->
                        logcat { "totalPageCount: $totalPageCount" }
                        when (book) {
                            is BookFile -> book.copy(totalPageCount = totalPageCount)
                            is BookFolder -> book.copy(totalPageCount = totalPageCount)
                        }.also {
                            fileLocalDataSource.addUpdate(it)
                        }
                    }
            }.fold(
                onSuccess = {
                    if (it != null) {
                        Resource.Success(it)
                    } else {
                        Resource.Error(Error.NotFound)
                    }
                },
                onFailure = {
                    logcat { it.asLog() }
                    // TODO Report Error
                    Resource.Error(Error.ReportedSystemError)
                },
            )
    }
}
