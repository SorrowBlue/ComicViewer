/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.receive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.common.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.BookFile
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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

private sealed interface PageAction {
    data class Init(val bookFile: BookFile) : PageAction
    data class PageLoaded(val unratedPage: UnratedPage, val isPortrait: Boolean) : PageAction
}

@AssistedInject
internal class ReceiveBookViewModel(
    @Assisted uri: String?,
    getIntentBookUseCase: GetIntentBookUseCase,
    manageViewerSettingsUseCase: ManageViewerSettingsUseCase,
    private val createInitialBookPagesUseCase: CreateInitialBookPagesUseCase,
    private val resolveBookPageLayoutUseCase: ResolveBookPageLayoutUseCase,
) : ViewModel() {

    private val pageActionFlow = MutableSharedFlow<PageAction>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val viewerSettingsFlow = manageViewerSettingsUseCase.settings
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val bookFlow = if (uri == null) {
        flowOf(null)
    } else {
        getIntentBookUseCase(GetIntentBookUseCase.Request(uri)).map { it.dataOrNull() }
    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val pageItemListFlow: StateFlow<List<PageItem>> = merge(
        bookFlow.filterNotNull().map { PageAction.Init(it) },
        pageActionFlow,
    ).scan(emptyList<PageItem>()) { currentList, action ->
        when (action) {
            is PageAction.Init -> {
                createInitialBookPagesUseCase(
                    totalPageCount = action.bookFile.totalPageCount,
                    pageFormat = BookSettings.PageFormat.Default,
                    isCompactWindow = false,
                )
            }

            is PageAction.PageLoaded -> {
                resolveBookPageLayoutUseCase(currentList, action.unratedPage, action.isPortrait)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    fun onPageLoaded(unratedPage: UnratedPage, isPortrait: Boolean) {
        pageActionFlow.tryEmit(PageAction.PageLoaded(unratedPage, isPortrait))
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(uri: String?): ReceiveBookViewModel
    }
}
