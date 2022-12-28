package com.sorrowblue.comicviewer.domain.usecase.interactor

import android.util.Log
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.model.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Response
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ServerRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.GetHistoryUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.first

internal class GetHistoryInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
    private val serverRepository: ServerRepository,
    private val fileRepository: FileRepository,
) : GetHistoryUseCase() {
    override suspend fun run(request: EmptyRequest): Response<Triple<Server, List<Bookshelf>, Int>> {
        val history = settingsCommonRepository.history.first()
        if (history.serverId == null || history.path == null) return Response.Error(Exception("履歴なし"))
        val library = serverRepository.get(history.serverId!!)
        return library.fold({
                lib ->
            if (lib == null) {
                return Response.Error(Exception("履歴が見つかりませんでした。libraryID=${history.serverId}"))
            } else {
                val a= getBookShelfList(lib, history.path!!).fold({
                        Response.Success(Triple(lib ,it, history.position ?: 0))
                    }, {
                        Response.Error(Exception("履歴が見つかりませんでした。libraryID=${history.serverId}"))
                    })
                return a
            }
        }, {
            Response.Error(Exception("履歴が見つかりませんでした。libraryID=${history.serverId}"))
        }, {
            Response.Error(Exception("履歴が見つかりませんでした。libraryID=${history.serverId}"))
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

