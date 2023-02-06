package com.sorrowblue.comicviewer.domain.interactor

import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Bookshelf
import com.sorrowblue.comicviewer.domain.model.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.first

internal class GetNavigationHistoryInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
) : GetNavigationHistoryUseCase() {
    override suspend fun run(request: EmptyRequest): Result<NavigationHistory, Unit> {
        val history = settingsCommonRepository.history.first()
        if (history.bookshelfId == null || history.path == null) return Result.Error(
            Unit
        )
        val library = bookshelfRepository.get(history.bookshelfId!!).first()
        return library.fold({ lib ->
            val a = getFolderList(lib, history.path!!).fold({
                Result.Success(NavigationHistory(lib, it, history.position ?: 0))
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

