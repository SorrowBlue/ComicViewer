/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileSizeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFolderBookThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@AssistedInject
internal class FileInfoViewModel(
    @Assisted private val file: File,
    getFileAttributeUseCase: GetFileAttributeUseCase,
    existsReadlaterUseCase: ExistsReadlaterUseCase,
    getFileSizeUseCase: GetFileSizeUseCase,
    pagingFolderBookThumbnailsUseCase: PagingFolderBookThumbnailsUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    private val deleteReadLaterUseCase: DeleteReadLaterUseCase,
) : ViewModel() {

    val fileAttributeFlow =
        getFileAttributeUseCase(GetFileAttributeUseCase.Request(file.bookshelfId, file.path))
            .map { it.dataOrNull() }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    val isReadLaterFlow =
        existsReadlaterUseCase(ExistsReadlaterUseCase.Request(file.bookshelfId, file.path))
            .map { it.dataOrNull() ?: false }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val fileSizeFlow =
        getFileSizeUseCase(GetFileSizeUseCase.Request(file.bookshelfId, file.path))
            .map { it.dataOrNull() ?: -1L }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    val pagingFlow = if (file is Book) {
        null
    } else {
        pagingFolderBookThumbnailsUseCase(
            PagingFolderBookThumbnailsUseCase.Request(
                file.bookshelfId,
                file.path,
                PagingConfig(10),
            ),
        ).cachedIn(viewModelScope)
    }

    fun updateReadLater() {
        viewModelScope.launch {
            delay(300.milliseconds)
            if (isReadLaterFlow.value) {
                deleteReadLaterUseCase(DeleteReadLaterUseCase.Request(file.bookshelfId, file.path))
            } else {
                addReadLaterUseCase(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
            }
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(file: File): FileInfoViewModel
    }
}
