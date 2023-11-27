package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.usecase.FlowUseCase2
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError

abstract class GetNextBookUseCase :
    FlowUseCase2<GetNextBookUseCase.Request, Book, GetLibraryInfoError>() {

    class Request(
        val bookshelfId: BookshelfId,
        val path: String,
        val location: Location,
        val isNext: Boolean,
    ) :
        BaseRequest

    sealed interface Location {
        data object Folder : Location
        data class Favorite(val favoriteId: FavoriteId) : Location
    }
}
