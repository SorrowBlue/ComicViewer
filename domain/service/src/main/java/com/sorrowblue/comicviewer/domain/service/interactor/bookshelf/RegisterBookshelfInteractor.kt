package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.service.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Resource
import com.sorrowblue.comicviewer.framework.fold
import com.sorrowblue.comicviewer.framework.onError
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * サーバに接続して、接続できたら登録/更新する
 *
 * @property bookshelfRepository
 */
internal class RegisterBookshelfInteractor @Inject constructor(
    private val fileRepository: FileRepository,
    private val bookshelfRepository: BookshelfRepository
) : RegisterBookshelfUseCase() {

    override fun run(request: Request): Flow<Resource<Bookshelf, Error>> {
        return flow {
            bookshelfRepository.connect(request.bookshelf, request.path).first().onError {
                emit(
                    Resource.Error(
                        when (it) {
                            BookshelfRepository.Error.Network -> Error.Network
                            BookshelfRepository.Error.System -> Error.Auth
                            BookshelfRepository.Error.NotFound -> Error.Path
                        }
                    )
                )
            }
            val registerResource = fileRepository
                .getFolder(request.bookshelf, request.path)
                .fold(
                    { folder ->
                        val root = fileRepository.getRoot(request.bookshelf.id).dataOrNull
                        if (root != null && root.path != folder.path) {
                            // 別の本棚を登録する場合、一旦削除
                            fileRepository.deleteAllCache(request.bookshelf.id)
                            fileRepository.deleteAllDB(request.bookshelf.id)
                        }
                        bookshelfRepository
                            .register(request.bookshelf, folder)
                            .first()
                            .fold(
                                {
                                    Resource.Success(it)
                                },
                                {
                                    Resource.Error(
                                        when (it) {
                                            BookshelfRepository.Error.NotFound -> Error.Path
                                            BookshelfRepository.Error.Network -> Error.Network
                                            BookshelfRepository.Error.System -> Error.Auth
                                        }
                                    )
                                }
                            )
                    },
                    {
                        Resource.Error(Error.System)
                    },
                    {
                        Resource.Error(Error.System)
                    }
                )
            emit(registerResource)
        }
    }
}
