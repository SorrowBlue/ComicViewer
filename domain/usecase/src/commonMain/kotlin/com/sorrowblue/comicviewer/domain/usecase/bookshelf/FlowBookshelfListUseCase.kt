package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.usecase.UseCase

abstract class FlowBookshelfListUseCase :
    UseCase<EmptyRequest, List<Bookshelf>, FlowBookshelfListUseCase.Error>() {
    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}
