/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.wrapper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

@AssistedInject
internal class BookWrapperViewModel(
    @Assisted bookshelfId: BookshelfId,
    @Assisted path: String,
    @Assisted collectionId: CollectionId,
    getBookUseCase: GetBookUseCase,
    manageViewerSettingsUseCase: ManageViewerSettingsUseCase,
) : ViewModel() {

    val viewerSettingsFlow =
        manageViewerSettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    val bookFlow = getBookUseCase(GetBookUseCase.Request(bookshelfId, path))
        .map { it.dataOrNull() }
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(
            bookshelfId: BookshelfId,
            path: String,
            collectionId: CollectionId,
        ): BookWrapperViewModel
    }
}
