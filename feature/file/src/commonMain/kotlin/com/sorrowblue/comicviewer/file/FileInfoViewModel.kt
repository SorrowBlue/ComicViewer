/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileSizeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFolderBookThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.file.section.FileInfoButtonsUiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal sealed interface FileInfoUiState {
    data object Loading : FileInfoUiState
    data class Success(
        val file: File,
        val attribute: FileAttribute? = null,
        val fileInfoButtonsUiState: FileInfoButtonsUiState = FileInfoButtonsUiState(),
    ) : FileInfoUiState

    data object Error : FileInfoUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
@AssistedInject
internal class FileInfoViewModel(
    @Assisted private val fileKey: File.Key,
    @Assisted private val isOpenFolderEnabled: Boolean,
    private val getFileUseCase: GetFileUseCase,
    private val getFileAttributeUseCase: GetFileAttributeUseCase,
    private val existsReadlaterUseCase: ExistsReadlaterUseCase,
    private val getFileSizeUseCase: GetFileSizeUseCase,
    private val pagingFolderBookThumbnailsUseCase: PagingFolderBookThumbnailsUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    private val deleteReadLaterUseCase: DeleteReadLaterUseCase,
) : ViewModel() {

    private val fileFlow = getFileUseCase(GetFileUseCase.Request(fileKey.bookshelfId, fileKey.path))
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val attributeFlow =
        getFileAttributeUseCase(GetFileAttributeUseCase.Request(fileKey.bookshelfId, fileKey.path))
            .map { it.dataOrNull() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val isReadLaterFlow =
        existsReadlaterUseCase(ExistsReadlaterUseCase.Request(fileKey.bookshelfId, fileKey.path))
            .map { it.dataOrNull() ?: false }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val isReadLaterLoading = MutableStateFlow(false)

    private val fileSizeFlow =
        getFileSizeUseCase(GetFileSizeUseCase.Request(fileKey.bookshelfId, fileKey.path))
            .map { it.dataOrNull() ?: -1L }
            .stateIn(viewModelScope, SharingStarted.Eagerly, -1L)

    val pagingFlow: Flow<PagingData<BookThumbnail>> = fileFlow.flatMapLatest { fileResource ->
        val file = fileResource?.dataOrNull()
        if (file != null && file !is Book) {
            pagingFolderBookThumbnailsUseCase(
                PagingFolderBookThumbnailsUseCase.Request(
                    file.bookshelfId,
                    file.path,
                    PagingConfig(10),
                ),
            )
        } else {
            emptyFlow()
        }
    }.cachedIn(viewModelScope)

    val uiState: StateFlow<FileInfoUiState> = combine(
        fileFlow,
        attributeFlow,
        isReadLaterFlow,
        isReadLaterLoading,
        fileSizeFlow,
    ) { fileRes, attribute, isReadLater, readLaterLoading, fileSize ->
        if (fileRes == null) {
            FileInfoUiState.Loading
        } else {
            when (fileRes) {
                is Resource.Error -> FileInfoUiState.Error

                is Resource.Success -> {
                    val updatedFile = when (val originalFile = fileRes.data) {
                        is BookFile -> originalFile.copy(size = fileSize)
                        is BookFolder -> originalFile.copy(size = fileSize)
                        is Folder -> originalFile.copy(size = fileSize)
                    }
                    FileInfoUiState.Success(
                        file = updatedFile,
                        attribute = attribute,
                        fileInfoButtonsUiState = FileInfoButtonsUiState(
                            isOpenFolderEnabled = isOpenFolderEnabled,
                            readLaterChecked = isReadLater,
                            readLaterLoading = readLaterLoading,
                        ),
                    )
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FileInfoUiState.Loading)

    fun onReadLaterClick() {
        val currentUiState = uiState.value
        if (currentUiState is FileInfoUiState.Success) {
            isReadLaterLoading.value = true
            viewModelScope.launch {
                delay(300)
                if (currentUiState.fileInfoButtonsUiState.readLaterChecked) {
                    deleteReadLaterUseCase(
                        DeleteReadLaterUseCase.Request(
                            fileKey.bookshelfId,
                            fileKey.path,
                        ),
                    )
                } else {
                    addReadLaterUseCase(
                        AddReadLaterUseCase.Request(
                            fileKey.bookshelfId,
                            fileKey.path,
                        ),
                    )
                }
                isReadLaterLoading.value = false
            }
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(fileKey: File.Key, isOpenFolderEnabled: Boolean): FileInfoViewModel
    }
}
