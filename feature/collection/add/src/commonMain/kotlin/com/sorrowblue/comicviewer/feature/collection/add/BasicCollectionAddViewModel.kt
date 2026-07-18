/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionType
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionExistUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.CollectionSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@AssistedInject
internal class BasicCollectionAddViewModel(
    @Assisted private val bookshelfId: BookshelfId,
    @Assisted private val path: String,
    pagingCollectionExistUseCase: PagingCollectionExistUseCase,
    private val collectionSettingsUseCase: CollectionSettingsUseCase,
    private val removeCollectionFileUseCase: RemoveCollectionFileUseCase,
    private val addCollectionFileUseCase: AddCollectionFileUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingCollectionExistUseCase(
        PagingCollectionExistUseCase.Request(
            pagingConfig = PagingConfig(PageSize),
            bookshelfId = bookshelfId,
            path = path,
            collectionType = CollectionType.Basic,
        ),
    ).cachedIn(viewModelScope)

    val collectionSettingsFlow =
        collectionSettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun updateCollectionSettings(action: (CollectionSettings) -> CollectionSettings) {
        viewModelScope.launch {
            collectionSettingsUseCase.edit(action)
        }
    }

    fun addCollection(id: CollectionId) {
        viewModelScope.launch {
            addCollectionFileUseCase(
                AddCollectionFileUseCase.Request(
                    CollectionFile(id, bookshelfId, path),
                ),
            )
        }
    }

    fun removeCollection(id: CollectionId) {
        viewModelScope.launch {
            removeCollectionFileUseCase(
                RemoveCollectionFileUseCase.Request(
                    CollectionFile(id, bookshelfId, path),
                ),
            )
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(bookshelfId: BookshelfId, path: String): BasicCollectionAddViewModel
    }
}

private const val PageSize = 20
