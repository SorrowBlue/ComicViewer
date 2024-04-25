package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class GetNavigationHistoryInteractor @Inject constructor(
    private val fileLocalDataSource: FileLocalDataSource,
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
) : GetNavigationHistoryUseCase() {
    override fun run(request: EmptyRequest): Flow<Resource<NavigationHistory, Error>> {
        return fileLocalDataSource.lastHistory().map { file ->
            val bookshelf = bookshelfLocalDataSource.flow(file.bookshelfId).first()
            if (bookshelf != null) {
                val book = fileLocalDataSource.findBy(file.bookshelfId, file.path) as? Book
                    ?: return@map Resource.Error(Error.System)
                getFolderList(bookshelf, book.parent).fold({
                    Resource.Success(NavigationHistory(it, book))
                }, {
                    Resource.Error(Error.System)
                })
            } else {
                Resource.Error(Error.System)
            }
        }
    }

    private suspend fun getFolderList(
        bookshelf: Bookshelf,
        path: String,
    ): Response.Success<List<Folder>> {
        val list = mutableListOf<Folder>()
        var parent: String? = path
        while (!parent.isNullOrEmpty()) {
            getFolder(bookshelf, parent)?.let {
                list.add(0, it)
                parent = it.parent
            } ?: kotlin.run {
                parent = null
                return Response.Success(emptyList())
            }
        }
        return Response.Success(list)
    }

    private suspend fun getFolder(bookshelf: Bookshelf, path: String): Folder? {
        return fileLocalDataSource.findBy(bookshelf.id, path) as? Folder
    }
}
