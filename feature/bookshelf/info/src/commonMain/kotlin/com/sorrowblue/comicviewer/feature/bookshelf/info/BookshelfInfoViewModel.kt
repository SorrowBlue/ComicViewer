package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn

@AssistedInject
internal class BookshelfInfoViewModel(
    @Assisted bookshelfId: BookshelfId,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
) : ViewModel() {

    val bookshelfInfo =
        getBookshelfInfoUseCase(
            GetBookshelfInfoUseCase.Request(bookshelfId = bookshelfId),
        ).mapNotNull { it.dataOrNull() }
            .shareIn(viewModelScope, started = SharingStarted.Eagerly, replay = 1)

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(bookshelfId: BookshelfId): BookshelfInfoViewModel
    }
}
