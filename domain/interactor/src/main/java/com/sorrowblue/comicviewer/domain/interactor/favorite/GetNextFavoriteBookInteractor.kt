package com.sorrowblue.comicviewer.domain.interactor.favorite

import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetNextFavoriteBookUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetNextFavoriteBookInteractor @Inject constructor(
    private val favoriteFileRepository: FavoriteFileRepository,
) : GetNextFavoriteBookUseCase() {

    override fun run(request: Request): Flow<Result<Book, GetLibraryInfoError>> {
        return favoriteFileRepository.getNextRelFile(
            request.favoriteFile,
            request.relation == GetNextComicRel.NEXT
        ).map { result ->
            result.fold({
                when (it) {
                    is Folder -> Result.Error(GetLibraryInfoError.NOT_FOUND)
                    is Book -> Result.Success(it)
                }
            }, {
                Result.Error(GetLibraryInfoError.NOT_FOUND)
            }, {
                Result.Error(GetLibraryInfoError.SYSTEM_ERROR)
            })
        }
    }
}
