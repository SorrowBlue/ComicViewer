package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class GetNextBookUseCase :
    OneShotUseCase<GetNextBookUseCase.Request, Book, GetNextBookUseCase.Error>() {
    class Request(
        val bookshelfId: BookshelfId,
        val path: String,
        val location: Location,
        val isNext: Boolean,
    ) : OneShotUseCase.Request

    sealed interface Location {
        data object Folder : Location

        data class Collection(val collectionId: CollectionId) : Location
    }

    enum class Error : Resource.AppError {
        System,
        NotFound,
    }
}
