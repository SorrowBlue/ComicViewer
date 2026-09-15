/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.book.NextBook
import com.sorrowblue.comicviewer.domain.model.book.NextPage
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.usecase.book.CreateInitialBookPagesUseCase
import com.sorrowblue.comicviewer.domain.usecase.book.ResolveBookPageLayoutUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.CloseBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private sealed interface PageAction {
    data class FormatChange(val pageFormat: BookSettings.PageFormat) : PageAction
    data class PageLoaded(val unratedPage: UnratedPage, val isPortrait: Boolean) : PageAction
}

@AssistedInject
internal class BookViewModel(
    @Assisted private val book: Book,
    @Assisted private val collectionId: CollectionId,
    @Assisted private val isCompactWindowClass: Boolean,
    manageBookSettingsUseCase: ManageBookSettingsUseCase,
    manageViewerSettingsUseCase: ManageViewerSettingsUseCase,
    val getNextBookUseCase: GetNextBookUseCase,
    val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
    val closeBookUseCase: CloseBookUseCase,
    private val createInitialBookPagesUseCase: CreateInitialBookPagesUseCase,
    private val resolveBookPageLayoutUseCase: ResolveBookPageLayoutUseCase,
) : ViewModel() {

    private val pageActionFlow = MutableSharedFlow<PageAction>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val bookSettingsFlow =
        manageBookSettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    val pageItemListFlow: StateFlow<List<PageItem>> = merge(
        bookSettingsFlow
            .distinctUntilChanged { old, new -> old.pageFormat == new.pageFormat }
            .map { PageAction.FormatChange(it.pageFormat) },
        pageActionFlow,
    ).scan(emptyList<PageItem>()) { currentList, action ->
        when (action) {
            is PageAction.FormatChange -> {
                val pages = createInitialBookPagesUseCase(
                    CreateInitialBookPagesUseCase.Request(
                        totalPageCount = book.totalPageCount,
                        pageFormat = action.pageFormat,
                        isCompactWindow = isCompactWindowClass,
                    ),
                ).dataOrNull()
                requireNotNull(pages)
                val prevBooks = getNextBookUseCase.execute(false)
                val nextBooks = getNextBookUseCase.execute(true)
                buildList {
                    add(NextPage(false, prevBooks))
                    addAll(pages)
                    add(NextPage(true, nextBooks))
                }
            }

            is PageAction.PageLoaded -> {
                resolveBookPageLayoutUseCase(
                    ResolveBookPageLayoutUseCase.Request(
                        currentList,
                        action.unratedPage,
                        action.isPortrait,
                    ),
                ).dataOrNull().let {
                    requireNotNull(it)
                }
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

    val viewerSettingsFlow =
        manageViewerSettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    private suspend fun GetNextBookUseCase.execute(isNext: Boolean): List<NextBook> = buildList {
        if (collectionId != CollectionId.Companion()) {
            invoke(
                GetNextBookUseCase.Request(
                    book.bookshelfId,
                    book.path,
                    GetNextBookUseCase.Location.Collection(collectionId),
                    isNext,
                ),
            ).dataOrNull()?.let {
                add(NextBook.Collection(it))
            }
        }
        invoke(
            GetNextBookUseCase.Request(
                book.bookshelfId,
                book.path,
                GetNextBookUseCase.Location.Folder,
                isNext,
            ),
        ).dataOrNull()?.let {
            add(NextBook.Folder(it))
        }
    }

    fun updateLastReadPage(page: Int) {
        viewModelScope.launch {
            updateLastReadPageUseCase(
                UpdateLastReadPageUseCase.Request(
                    book.bookshelfId,
                    book.path,
                    page - 1,
                ),
            )
        }
    }

    fun release() {
        viewModelScope.launch {
            closeBookUseCase(book)
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(
            book: Book,
            collectionId: CollectionId,
            isCompactWindowClass: Boolean,
        ): BookViewModel
    }
}
