/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.ImageCache
import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetBookshelfImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetOtherImageCacheInfoUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class ImageCacheViewModel(
    getBookshelfImageCacheInfoUseCase: GetBookshelfImageCacheInfoUseCase,
    getOtherImageCacheInfoUseCase: GetOtherImageCacheInfoUseCase,
    private val clearImageCacheUseCase: ClearImageCacheUseCase,
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookshelfImageCacheInfoFlow = refreshTrigger.flatMapLatest { _ ->
        getBookshelfImageCacheInfoUseCase(GetBookshelfImageCacheInfoUseCase.Request).mapNotNull {
            it.dataOrNull()
        }
    }.shareIn(viewModelScope, SharingStarted.Lazily)

    val otherImageCacheInfoFlow = refreshTrigger.mapNotNull { _ ->
        getOtherImageCacheInfoUseCase(GetOtherImageCacheInfoUseCase.Request).dataOrNull()
    }.shareIn(viewModelScope, SharingStarted.Lazily)

    val eventFlow: SharedFlow<ImageCacheViewModelEvent>
        field = MutableSharedFlow()

    init {
        refresh()
    }

    fun clearImageCache(bookshelfId: BookshelfId, imageCache: ImageCache) {
        val request = when (imageCache) {
            is ThumbnailImageCache ->
                ClearImageCacheUseCase.BookshelfRequest(bookshelfId, imageCache)

            is BookPageImageCache ->
                ClearImageCacheUseCase.BookshelfRequest(bookshelfId, imageCache)

            is OtherImageCache -> ClearImageCacheUseCase.OtherRequest
        }
        viewModelScope.launch {
            clearImageCacheUseCase(request)
            refresh()
            eventFlow.emit(ImageCacheViewModelEvent.CompleteClearImageCache)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }
}

internal interface ImageCacheViewModelEvent {
    data object CompleteClearImageCache : ImageCacheViewModelEvent
}
