package com.sorrowblue.comicviewer.domain.usecase.interactor

import android.util.Log
import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.model.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import javax.inject.Inject
import kotlinx.coroutines.flow.first

internal class GetNavigationHistoryInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository,
) : GetNavigationHistoryUseCase() {
    override suspend fun run(request: EmptyRequest): Result<NavigationHistory, Unit> {
        val history = settingsCommonRepository.history.first()
        if (history.serverId == null || history.path == null) return Result.Error(
            Unit
        )
        val library = serverRepository.get(history.serverId!!)
        return library.fold({ lib ->
            if (lib == null) {
                Exception("履歴が見つかりませんでした。libraryID=${history.serverId}")
                return Result.Error(Unit)
            } else {
                val a = getBookShelfList(lib, history.path!!).fold({
                    Result.Success(NavigationHistory(lib, it, history.position ?: 0))
                }, {
                    Exception("履歴が見つかりませんでした。libraryID=${history.serverId}")
                    Result.Error(Unit)
                })
                return a
            }
        }, {
            Exception("履歴が見つかりませんでした。libraryID=${history.serverId}")
            Result.Error(Unit)
        }, {
            Exception("履歴が見つかりませんでした。libraryID=${history.serverId}")
            Result.Error(Unit)
        })
    }

    private suspend fun getBookShelfList(
        server: Server,
        path: String,
    ): Response.Success<List<Bookshelf>> {
        Log.d("TAG", "getBookShelfList: $path")
        val list = mutableListOf<Bookshelf>()
        var parent: String? = path
        while (!parent.isNullOrEmpty()) {
            getBookShelf(server, parent)?.let {
                list.add(0, it)
                parent = it.parent
            } ?: kotlin.run {
                parent = null
                return Response.Success(emptyList())
            }
        }
        return Response.Success(list)
    }

    private suspend fun getBookShelf(server: Server, path: String): Bookshelf? {
        fileRepository.get(server.id, path).onSuccess {
            return it as? Bookshelf
        }.onError {
            return null
        }
        return null
    }
}

