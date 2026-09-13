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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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

    private val mutex = Mutex()
    private val _pageItemListFlow = MutableStateFlow<List<PageItem>>(emptyList())
    val pageItemListFlow = _pageItemListFlow.asStateFlow()

    val bookSettingsFlow =
        manageBookSettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    init {
        viewModelScope.launch {
            bookSettingsFlow
                .distinctUntilChanged { old, new -> old.pageFormat == new.pageFormat }
                .collect { settings ->
                    val pages = createInitialBookPagesUseCase(
                        totalPageCount = book.totalPageCount,
                        pageFormat = settings.pageFormat,
                        isCompactWindow = isCompactWindowClass,
                    )
                    val prevBooks = getNextBookUseCase.execute(false)
                    val nextBooks = getNextBookUseCase.execute(true)
                    val list = buildList {
                        add(NextPage(false, prevBooks))
                        addAll(pages)
                        add(NextPage(true, nextBooks))
                    }
                    mutex.withLock {
                        _pageItemListFlow.value = list
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
            closeBookUseCase(CloseBookUseCase.Request(book))
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
