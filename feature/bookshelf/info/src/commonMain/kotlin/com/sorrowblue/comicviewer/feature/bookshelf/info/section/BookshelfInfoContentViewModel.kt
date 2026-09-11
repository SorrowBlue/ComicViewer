/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

private const val PageSize = 4

@AssistedInject
internal class BookshelfInfoContentViewModel(
    @Assisted private val bookshelfFolder: BookshelfFolder,
    private val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase,
    private val scanManager: BookshelfScanManager,
) : ViewModel() {

    val pagingDataFlow = pagingBookshelfBookUseCase(
        PagingBookshelfBookUseCase.Request(
            bookshelfFolder.bookshelf.id,
            PagingConfig(PageSize),
        ),
    ).cachedIn(viewModelScope)

    val isScanningFile: StateFlow<Boolean> =
        scanManager.isScanningFile(bookshelfFolder.bookshelf.id)
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                false,
            )

    val isScanningThumbnail: StateFlow<Boolean> =
        scanManager.isScanningThumbnail(bookshelfFolder.bookshelf.id)
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                false,
            )

    fun scanFile() {
        scanManager.scanFile(bookshelfFolder.bookshelf.id)
    }

    fun scanThumbnail() {
        scanManager.scanThumbnail(bookshelfFolder.bookshelf.id)
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(bookshelfFolder: BookshelfFolder): BookshelfInfoContentViewModel
    }
}
