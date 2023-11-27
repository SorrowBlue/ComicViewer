package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

internal class GetNavigationHistoryInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : GetNavigationHistoryUseCase() {
    override suspend fun run(request: EmptyRequest): Result<NavigationHistory, Unit> {
        val history = fileRepository.lastHistory().firstOrNull() ?: return Result.Error(Unit)
        val library = bookshelfRepository.get(history.bookshelfId).first()
        return library.fold({ lib ->
            val book = fileRepository.get(history.bookshelfId, history.path)
                .fold({ file -> file as? Book }, { null }) ?: return Result.Error(Unit)
            val a = getFolderList(lib, book.parent).fold({
                Result.Success(NavigationHistory(lib, it, book))
            }, {
                Result.Error(Unit)
            })
            return a
        }, {
            Result.Error(Unit)
        }, {
            Result.Error(Unit)
        })
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
        fileRepository.get(bookshelf.id, path).onSuccess {
            return it as? Folder
        }.onError {
            return null
        }
        return null
    }
}
