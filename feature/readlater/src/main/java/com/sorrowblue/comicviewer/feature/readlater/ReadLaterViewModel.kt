package com.sorrowblue.comicviewer.feature.readlater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterAction
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.file.component.toFileContentLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logcat.logcat

@HiltViewModel
internal class ReadLaterViewModel @Inject constructor(
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
    pagingReadLaterFileUseCase: PagingReadLaterFileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReadLaterScreenUiState(
            fileContentType = runBlocking {
                manageFolderDisplaySettingsUseCase.settings.first().toFileContentLayout()
            }
        )
    )

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.settings.map(FolderDisplaySettings::toFileContentLayout)
                .distinctUntilChanged().collectLatest {
                    val (old, new) = when (it) {
                        is FileContentType.Grid -> ReadLaterAction.FileContetView to ReadLaterAction.FileContetGrid
                        FileContentType.List -> ReadLaterAction.FileContetGrid to ReadLaterAction.FileContetView
                    }
                    _uiState.value = _uiState.value.copy(
                        list = uiState.value.list.map { if (it == old) new else it }
                            .toPersistentList(),
                        fileContentType = it
                    )
                }
        }
    }

    val pagingDataFlow = pagingReadLaterFileUseCase
        .execute(PagingReadLaterFileUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)

    fun toggleDisplay() {
        viewModelScope.launch {
            logcat { "toggleDisplay" }
            manageFolderDisplaySettingsUseCase.edit {
                it.copy(
                    display = when (it.display) {
                        FolderDisplaySettings.Display.GRID -> FolderDisplaySettings.Display.LIST
                        FolderDisplaySettings.Display.LIST -> FolderDisplaySettings.Display.GRID
                    }
                )
            }
        }
    }

    fun toggleSpanCount() {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                it.copy(
                    spanCount = when (it.spanCount) {
                        2 -> 3
                        3 -> 4
                        else -> 2
                    }
                )
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            deleteAllReadLaterUseCase.execute(DeleteAllReadLaterUseCase.Request).first()
        }
    }
}
