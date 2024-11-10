package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class GetNextBookUseCase :
    UseCase<GetNextBookUseCase.Request, Book, GetLibraryInfoError>() {

    class Request(
        val bookshelfId: BookshelfId,
        val path: String,
        val location: Location,
        val isNext: Boolean,
    ) : UseCase.Request

    sealed interface Location {
        data object Folder : Location
        data class Favorite(val favoriteId: FavoriteId) : Location
    }
}
