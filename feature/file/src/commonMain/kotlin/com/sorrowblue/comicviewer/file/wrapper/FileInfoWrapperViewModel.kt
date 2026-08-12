/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file.wrapper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AssistedInject
internal class FileInfoWrapperViewModel(
    @Assisted fileKey: File.Key,
    @Assisted isOpenFolderEnabled: Boolean,
    getFileUseCase: GetFileUseCase,
) : ViewModel() {

    val uiState = getFileUseCase(GetFileUseCase.Request(fileKey.bookshelfId, fileKey.path))
        .map { resource ->
            when (resource) {
                is Resource.Success<File> ->
                    FileInfoWrapperUiState.Success(resource.data, isOpenFolderEnabled)

                is Resource.Error<*> -> FileInfoWrapperUiState.Error
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, FileInfoWrapperUiState.Loading)

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(fileKey: File.Key, isOpenFolderEnabled: Boolean): FileInfoWrapperViewModel
    }
}
