package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class GetNavigationHistoryInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : GetNavigationHistoryUseCase() {
    override fun run(request: EmptyRequest): Flow<Resource<NavigationHistory, Error>> {
        return fileRepository.lastHistory().map { history ->
            val library = bookshelfRepository.get(history.bookshelfId).first()
            library.fold({ lib ->
                val book = fileRepository.get(history.bookshelfId, history.path)
                    .fold({ file -> file as? Book }, { null })
                    ?: return@fold Resource.Error(Error.System)
                val a = getFolderList(lib, book.parent).fold({
                    Resource.Success(NavigationHistory(it, book))
                }, {
                    Resource.Error(Error.System)
                })
                return@fold a
            }, {
                Resource.Error(Error.System)
            }, {
                Resource.Error(Error.System)
            })
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
        fileRepository.get(bookshelf.id, path).onSuccess {
            return it as? Folder
        }.onError {
            return null
        }
        return null
    }
}
