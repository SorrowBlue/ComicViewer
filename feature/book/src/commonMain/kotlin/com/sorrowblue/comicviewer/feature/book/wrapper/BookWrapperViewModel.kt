/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.wrapper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AssistedInject
internal class BookWrapperViewModel(
    @Assisted bookshelfId: BookshelfId,
    @Assisted path: String,
    @Assisted name: String,
    @Assisted collectionId: CollectionId,
    getBookUseCase: GetBookUseCase,
    manageViewerSettingsUseCase: ManageViewerSettingsUseCase,
) : ViewModel() {

    val uiStateFlow: StateFlow<BookScreenUiState> =
        getBookUseCase(GetBookUseCase.Request(bookshelfId, path))
            .map { resource ->
                val book = resource.dataOrNull()
                if (book != null) {
                    BookScreenUiState.Loaded(
                        book = book,
                        collectionId = collectionId,
                        bookSheetUiState = BookSheetUiState(book),
                        alwaysOpenFromFirstPage =
                            manageViewerSettingsUseCase.settings.first().alwaysOpenFromFirstPage,
                    )
                } else {
                    BookScreenUiState.Error(name)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = BookScreenUiState.Loading(name),
            )

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(
            bookshelfId: BookshelfId,
            path: String,
            name: String,
            collectionId: CollectionId,
        ): BookWrapperViewModel
    }
}
