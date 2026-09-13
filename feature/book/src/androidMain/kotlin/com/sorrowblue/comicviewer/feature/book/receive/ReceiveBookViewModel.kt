/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.receive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.common.dataOrNull
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.usecase.book.CreateInitialBookPagesUseCase
import com.sorrowblue.comicviewer.domain.usecase.book.ResolveBookPageLayoutUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetIntentBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@AssistedInject
internal class ReceiveBookViewModel(
    @Assisted uri: String?,
    getIntentBookUseCase: GetIntentBookUseCase,
    manageViewerSettingsUseCase: ManageViewerSettingsUseCase,
    private val createInitialBookPagesUseCase: CreateInitialBookPagesUseCase,
    private val resolveBookPageLayoutUseCase: ResolveBookPageLayoutUseCase,
) : ViewModel() {

    private val mutex = Mutex()
    private val _pageItemListFlow = MutableStateFlow<List<PageItem>>(emptyList())
    val pageItemListFlow = _pageItemListFlow.asStateFlow()

    val viewerSettingsFlow = manageViewerSettingsUseCase.settings
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val bookFlow = if (uri == null) {
        flowOf(null)
    } else {
        getIntentBookUseCase(GetIntentBookUseCase.Request(uri)).map { it.dataOrNull() }
    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    init {
        viewModelScope.launch {
            bookFlow.collect { bookFile ->
                if (bookFile != null) {
                    val pages = createInitialBookPagesUseCase(
                        totalPageCount = bookFile.totalPageCount,
                        pageFormat = BookSettings.PageFormat.Default,
                        isCompactWindow = false,
                    )
                    mutex.withLock {
                        _pageItemListFlow.value = pages
                    }
                }
            }
        }
    }

    fun onPageLoaded(unratedPage: UnratedPage, isPortrait: Boolean) {
        viewModelScope.launch {
            mutex.withLock {
                val current = _pageItemListFlow.value
                val updated = resolveBookPageLayoutUseCase(current, unratedPage, isPortrait)
                if (updated !== current) {
                    _pageItemListFlow.value = updated
                }
            }
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(uri: String?): ReceiveBookViewModel
    }
}
