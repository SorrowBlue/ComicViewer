package com.sorrowblue.comicviewer.domain.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepositoryError
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

/**
 * サーバに接続して、接続できたら登録/更新する
 *
 * @property bookshelfRepository
 */
internal class RegisterBookshelfInteractor @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository
) : RegisterBookshelfUseCase() {

    override suspend fun run(request: Request): Result<Bookshelf, RegisterBookshelfError> {
        return bookshelfRepository.connect(request.bookshelf, request.path).fold({
            fileRepository.getFolder(request.bookshelf, request.path).fold({
                bookshelfRepository.register(request.bookshelf, it).fold({
                    Result.Success(it)
                }, {
                    when (it) {
                        BookshelfRepositoryError.AuthenticationFailure -> Result.Error(
                            RegisterBookshelfError.InvalidAuth
                        )
                        BookshelfRepositoryError.PathDoesNotExist -> Result.Error(
                            RegisterBookshelfError.InvalidPath)
                        BookshelfRepositoryError.IncorrectServerInfo -> Result.Error(
                            RegisterBookshelfError.InvalidBookshelfInfo
                        )
                    }
                }, {
                    Result.Exception(it)
                })
            }, {
                when (it) {
                    FileRepositoryError.AuthenticationFailure -> Result.Error(RegisterBookshelfError.InvalidAuth)
                    FileRepositoryError.PathDoesNotExist -> Result.Error(RegisterBookshelfError.InvalidPath)
                    FileRepositoryError.IncorrectServerInfo -> Result.Error(RegisterBookshelfError.InvalidBookshelfInfo)
                }
            }, {
                Result.Exception(it)
            })
        }, {
            when (it) {
                BookshelfRepositoryError.AuthenticationFailure -> Result.Error(RegisterBookshelfError.InvalidAuth)
                BookshelfRepositoryError.PathDoesNotExist -> Result.Error(RegisterBookshelfError.InvalidPath)
                BookshelfRepositoryError.IncorrectServerInfo -> Result.Error(RegisterBookshelfError.InvalidBookshelfInfo)
            }
        }, { Result.Exception(it) })
    }
}
