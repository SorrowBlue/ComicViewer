/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.usecase.file.CloseBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.NextBook
import com.sorrowblue.comicviewer.feature.book.section.NextPage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

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
) : ViewModel() {

    val bookSettingsFlow =
        manageBookSettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    val pageItemListFlow =
        bookSettingsFlow.distinctUntilChanged { old, new -> old.pageFormat == new.pageFormat }
            .map { settings ->
                buildList {
                    add(NextPage(false, getNextBookUseCase.execute(false)))
                    addAll(
                        when (settings.pageFormat) {
                            BookSettings.PageFormat.Default -> (1..book.totalPageCount)
                                .map {
                                    BookPage.Default(it - 1)
                                }

                            BookSettings.PageFormat.Spread ->
                                (1..book.totalPageCount).map {
                                    BookPage.Spread.Unrated(it - 1)
                                }

                            BookSettings.PageFormat.Split -> (1..book.totalPageCount)
                                .map {
                                    BookPage.Split.Unrated(it - 1)
                                }

                            BookSettings.PageFormat.Auto ->
                                if (isCompactWindowClass) {
                                    (1..book.totalPageCount).map {
                                        BookPage.Split.Unrated(it - 1)
                                    }
                                } else {
                                    (1..book.totalPageCount).map {
                                        BookPage.Spread.Unrated(it - 1)
                                    }
                                }
                        },
                    )
                    add(NextPage(true, getNextBookUseCase.execute(true)))
                }
            }.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

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
